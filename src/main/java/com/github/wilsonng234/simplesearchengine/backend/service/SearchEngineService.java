package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.PageRank;
import com.github.wilsonng234.simplesearchengine.backend.model.TermWeightsVector;
import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.util.NLPUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.SearchEngineUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.VSMUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private TermWeightsVectorService termWeightsVectorService;
    @Autowired
    private PageRankService pageRankService;
    private List<Word> words;
    private List<Document> documents;
    private List<Double> pageRanksVector;
    private List<List<Double>> documentsVector;     // index1: docId, index2: wordID, value: termWeight
    private List<Double> queryVector;               // index: wordID, value: termWeight
    @Getter
    private List<Double> scoresVector;              // index: docId, value: score
    private Map<String, Integer> wordsMap;          // key: wordId, value: index
    private Map<String, Integer> documentsMap;      // key: docId, value: index

    @Data
    @AllArgsConstructor
    public static class QueryResult {
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
            double w1 = 0.8;
            double w2 = 0.2;
            double cosineSimilarity = VSMUtils.getCosineSimilarity(documentVector, queryVector);
            double pageRank = pageRanksVector.get(i);
            double score = w1 * cosineSimilarity + w2 * pageRank;

            double epsilon = 0.000001d;
            if (Double.isNaN(score) || Math.abs(cosineSimilarity - 0.0) < epsilon)
                score = 0.0;

            scoresVector.set(i, score);
        }
    }

    private void setUp() {
        words = wordService.allWords();
        documents = documentService.allDocuments();

        int i = 0;
        documentsMap = new HashMap<>();
        scoresVector = Collections.synchronizedList(new ArrayList<>(documents.size()));
        pageRanksVector = Collections.synchronizedList(new ArrayList<>(documents.size()));
        for (Document document : documents) {
            String docId = document.getDocId();

            documentsMap.put(docId, i);
            scoresVector.add(0.0);

            Optional<PageRank> pageRankOptional = pageRankService.getPageRank(docId);
            if (pageRankOptional.isEmpty()) {
                pageRanksVector.add(1 / (double) documents.size());
            } else {
                pageRanksVector.add(pageRankOptional.get().getPageRank());
            }
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
        for (Document document : documents) {
            String docId = document.getDocId();
            Integer docIndex = documentsMap.get(docId);

            if (docIndex == null) {
                logger.error("Doc index is null");
                continue;
            }

            Optional<TermWeightsVector> termWeightsVector = termWeightsVectorService.getTermWeightsVector(docId);
            if (termWeightsVector.isEmpty()) {
                logger.error("Term weights vector for document " + docId + " not found");
                continue;
            }

            Map<String, Double> termWeights = termWeightsVector.get().getTermWeights();
            for (Word word : words) {
                String wordId = word.getWordId();
                Integer wordIndex = wordsMap.get(wordId);
                if (wordIndex == null) {
                    logger.error("Word index is null");
                    continue;
                }

                Double termWeight = termWeights.getOrDefault(wordId, 0.0);
                documentsVector.get(docIndex).set(wordIndex, termWeight);
            }
        }
    }
}
