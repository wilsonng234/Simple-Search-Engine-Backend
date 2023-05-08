package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.TermWeight;
import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.util.VSMUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.mongodb.DuplicateKeyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@Scope("prototype")
public class TermWeightService {
    private static final Logger logger = LogManager.getLogger(TermWeightService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<TermWeight> getTermWeight(String type, String docId, String wordId) {
        Query query = new Query(
                Criteria.where("type").is(type)
                        .and("docId").is(docId)
                        .and("wordId").is(wordId));
        return Optional.ofNullable(mongoTemplate.findOne(query, TermWeight.class));
    }

    public List<TermWeight> allTermWeights() {
        return mongoTemplate.findAll(TermWeight.class);
    }

    public TermWeight putTermWeight(TermWeight termWeight) {
        Query query = new Query(Criteria.where("docId").is(termWeight.getDocId()).and("wordId").is(termWeight.getWordId()));
        Update update = new Update()
                .set("termWeight", termWeight.getTermWeight());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<TermWeight> cls = TermWeight.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    public boolean updateTermWeights() {
        List<Document> documents = mongoTemplate.findAll(Document.class);
        List<Word> words = mongoTemplate.findAll(Word.class);

        BiMap<String, Integer> documentsBiMap = HashBiMap.create();
        int i = 0;
        for (Document document : documents) {
            documentsBiMap.put(document.getDocId(), i);
            i += 1;
        }

        i = 0;
        BiMap<String, Integer> wordsBiMap = HashBiMap.create();
        for (Word word : words) {
            wordsBiMap.put(word.getWordId(), i);
            i += 1;
        }

        List<List<Double>> documentsVector = Collections.synchronizedList(new ArrayList<>(documents.size()));
        for (i = 0; i < documents.size(); i++) {
            List<Double> documentVector = new ArrayList<>(words.size());
            for (int j = 0; j < words.size(); j++) {
                documentVector.add(0.0);
            }

            documentsVector.add(documentVector);
        }

        class UpdateTermWeightsByWords implements Runnable {
            final double titleWeight = 10.0;
            final int numDocs = documents.size();
            private final List<Word> words;

            public UpdateTermWeightsByWords(List<Word> words) {
                this.words = words;
            }

            @Override
            public void run() {
                for (Word word : words) {
                    String wordId = word.getWordId();
                    Integer wordIndex = wordsBiMap.get(wordId);

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
                        Integer docIndex = documentsBiMap.get(docId);
                        if (docIndex == null) {
                            logger.error("Doc index is null" + docId);
                            continue;
                        }
                        int titleTF = posting.getTf();
                        int titleMaxTF = documents.get(docIndex).getTitleMaxTF();

                        double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                        double additionTermWeight = titleWeight * VSMUtils.getTermWeight(titleTF, numDocs, titleDocFreq, titleMaxTF);
                        documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
                    }

                    int bodyDocFreq = bodyPostings.size();
                    for (Posting posting : bodyPostings) {
                        String docId = posting.getDocId();
                        Integer docIndex = documentsBiMap.get(docId);
                        if (docIndex == null) {
                            logger.error("Doc index is null" + docId);
                            continue;
                        }
                        int bodyTF = posting.getTf();
                        int bodyMaxTF = documents.get(docIndex).getBodyMaxTF();

                        double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                        double additionTermWeight = VSMUtils.getTermWeight(bodyTF, numDocs, bodyDocFreq, bodyMaxTF);
                        documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
                    }
                }

                for (int docIndex = 0; docIndex < documents.size(); docIndex++) {
                    List<Double> documentVector = documentsVector.get(docIndex);
                    String docId = documentsBiMap.inverse().get(docIndex);

                    for (int wordIndex = 0; wordIndex < documentVector.size(); wordIndex++) {
                        String wordId = wordsBiMap.inverse().get(wordIndex);

                        TermWeight termWeight = new TermWeight(docId, wordId, documentVector.get(wordIndex));
                        putTermWeight(termWeight);
                    }
                }
            }
        }

        synchronized (TermWeightService.class) {
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

        return true;
    }
}
