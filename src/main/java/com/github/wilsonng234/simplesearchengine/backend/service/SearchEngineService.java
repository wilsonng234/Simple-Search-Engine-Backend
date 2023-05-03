package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.util.NLPUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.SearchEngineUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.VSMUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class SearchEngineService {
    private static final Logger logger = LogManager.getLogger(SearchEngineService.class);

    @Autowired
    private WordService wordService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PostingListService postingListService;
    @Autowired
    private MongoTemplate mongoTemplate;
    private List<Word> words;
    private List<Document> documents;
    private List<List<Double>> documentsVector;     // index1: docId, index2: wordID, value: termWeight
    private List<Double> queryVector;               // index: wordID, value: termWeight
    @Getter
    private List<Double> scoresVector;              // index: docId, value: score
    private Map<String, Integer> wordsMap;          // key: wordId, value: index
    private Map<String, Integer> documentsMap;      // key: docId, value: index

    @Data
    @AllArgsConstructor
    public class QueryResult {
        private double score;
        private String docId;
        private String url;
        private long size;
        private String title;
        private long lastModificationDate;
        private List<Pair<String, Integer>> titleWordFreqs;      // word, frequency
        private List<Pair<String, Integer>> bodyWordFreqs;       // word, frequency
        private Set<String> childrenUrls;
    }

    public List<QueryResult> search(String query) {
        long start = System.currentTimeMillis();
        setUp();
        setUpQueryVector(query);
        setUpScoresVector();

        List<Integer> topKIndices = SearchEngineUtils.getTopKIndices(scoresVector, 50);

        List<QueryResult> queryResults = new ArrayList<>(topKIndices.size());
        for (Integer index : topKIndices) {
            Document document = documents.get(index);
            QueryResult queryResult = new QueryResult(
                    scoresVector.get(index),
                    document.getDocId(),
                    document.getUrl(),
                    document.getSize(),
                    document.getTitle(),
                    document.getLastModificationDate(),
                    document.getTitleWordFreqs(),
                    document.getBodyWordFreqs(),
                    document.getChildrenUrls()
            );

            queryResults.add(queryResult);
        }
        queryResults.sort(Comparator.comparingDouble(QueryResult::getScore).reversed());

        logger.info("Search time: " + (System.currentTimeMillis() - start) + "ms");
        return queryResults;
    }

    private void setUpQueryVector(String query) {
        List<String> normalWords = NLPUtils.tokenize(query);
        normalWords = NLPUtils.removePunctuations(normalWords, true);
        normalWords = NLPUtils.stemWords(normalWords);
        normalWords = NLPUtils.removeStopWords(normalWords);

        for (String normalWord : normalWords) {
            Optional<Word> wordIdOptional = wordService.getWord(normalWord, WordService.QueryType.WORD);
            if (wordIdOptional.isPresent()) {
                String wordId = wordIdOptional.get().getWordId();
                Integer wordIndex = wordsMap.get(wordId);

                if (wordIndex != null)
                    queryVector.set(wordIndex, queryVector.get(wordIndex) + 1.0);
            }
        }

        List<String> phraseWords = NLPUtils.parsePhraseSearchQuery(query);
        phraseWords = phraseWords.stream().map(
                words -> {
                    List<String> wordsList = NLPUtils.tokenize(words);
                    wordsList = NLPUtils.removePunctuations(wordsList, true);
                    wordsList = NLPUtils.stemWords(wordsList);

                    return String.join(" ", wordsList);
                }
        ).collect(Collectors.toCollection(LinkedList::new));

        for (String phrase : phraseWords) {
            Optional<Word> wordIdOptional = wordService.getWord(phrase, WordService.QueryType.WORD);

            if (wordIdOptional.isPresent()) {
                String wordId = wordIdOptional.get().getWordId();
                Integer wordIndex = wordsMap.get(wordId);

                if (wordIndex != null) {
                    queryVector.set(wordIndex, queryVector.get(wordIndex) + 10.0);
                }
            }
        }

        System.out.println("normalWords: " + normalWords);
        System.out.println("phraseWords: " + phraseWords);
    }

    private void setUpScoresVector() {
        for (int i = 0; i < documents.size(); i++) {
            List<Double> documentVector = documentsVector.get(i);
            double score = VSMUtils.getCosineSimilarity(documentVector, queryVector);

            if (!Double.isNaN(score))
                scoresVector.set(i, score);
        }
    }

    private void setUp() {
        words = wordService.allWords();
        documents = documentService.allDocuments();

        int i = 0;
        documentsMap = new HashMap<>();
        scoresVector = Collections.synchronizedList(new ArrayList<>(documents.size()));
        for (Document document : documents) {
            documentsMap.put(document.getDocId(), i);
            scoresVector.add(0.0);
            i += 1;
        }

        i = 0;
        wordsMap = new HashMap<>();
        queryVector = Collections.synchronizedList(new ArrayList<>(words.size()));
        for (Word word : words) {
            wordsMap.put(word.getWordId(), i);
            queryVector.add(0.0);
            i += 1;
        }

        documentsVector = Collections.synchronizedList(new ArrayList<>(documents.size()));
        for (i = 0; i < documents.size(); i++) {
            List<Double> documentVector = new ArrayList<>(words.size());
            for (int j = 0; j < words.size(); j++) {
                documentVector.add(0.0);
            }

            documentsVector.add(documentVector);
        }

        setUpDocumentsVector();
    }

    private void setUpDocumentsVector() {
        double titleWeight = 10.0;
        int numDocs = documents.size();

        class UpdateTermWeightsByWords implements Runnable {
            private final List<Word> words;

            public UpdateTermWeightsByWords(List<Word> words) {
                this.words = words;
            }

            @Override
            public void run() {
                for (Word word : words) {
                    String wordId = word.getWordId();
                    Integer wordIndex = wordsMap.get(wordId);

                    if (wordIndex == null) {
                        logger.error("Word index is null" + word.getWord());
                        continue;
                    }

                    List<Posting> titlePostings = mongoTemplate.find(
                            Query.query(
                                    Criteria.where("type").is("title")
                                            .and("wordId").is(wordId)),
                            Posting.class
                    );
                    List<Posting> bodyPostings = mongoTemplate.find(
                            Query.query(Criteria.where("type").is("body")
                                    .and("wordId").is(wordId)),
                            Posting.class
                    );

                    int titleDocFreq = titlePostings.size();
                    for (Posting posting : titlePostings) {
                        String docId = posting.getDocId();
                        Integer docIndex = documentsMap.get(docId);
                        if (docIndex == null) {
                            logger.error("Doc index is null" + docId);
                            continue;
                        }
                        int maxTF = documents.get(docIndex).getMaxTF();
                        int tf = posting.getTf();

                        double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                        double additionTermWeight = titleWeight * VSMUtils.getTermWeight(tf, numDocs, titleDocFreq, maxTF);
                        documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
                    }

                    int bodyDocFreq = bodyPostings.size();
                    for (Posting posting : bodyPostings) {
                        String docId = posting.getDocId();
                        Integer docIndex = documentsMap.get(docId);
                        if (docIndex == null) {
                            logger.error("Doc index is null" + docId);
                            continue;
                        }
                        int maxTF = documents.get(docIndex).getMaxTF();
                        int tf = posting.getTf();

                        double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                        double additionTermWeight = VSMUtils.getTermWeight(tf, numDocs, bodyDocFreq, maxTF);
                        documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
                    }
                }
            }
        }

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newCachedThreadPool();

        double taskThreadsRatio = 1d;     // taskThreadsRatio: (num tasks / num available threads)
        int chunkSize = (int) Math.ceil((double) words.size() / numThreads / taskThreadsRatio);
        List<List<Word>> wordsChunks = Lists.partition(words, chunkSize);
        List<Future<?>> futures = new ArrayList<>(wordsChunks.size());
        for (List<Word> chunk : wordsChunks)
            futures.add(executorService.submit(new UpdateTermWeightsByWords(chunk)));

        executorService.shutdown();     // stop accepting new tasks
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage());
            executorService.shutdownNow();
        }

        // Handle tasks exceptions
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
