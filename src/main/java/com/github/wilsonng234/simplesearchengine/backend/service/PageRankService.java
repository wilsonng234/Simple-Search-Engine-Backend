package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.PageRank;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;

@Service
@Scope("prototype")
public class PageRankService {
    private static final Logger logger = LogManager.getLogger(PageRankService.class);
    @Autowired
    private MongoTemplate mongoTemplate;


    public Optional<PageRank> getPageRank(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return Optional.ofNullable(mongoTemplate.findOne(query, PageRank.class));
    }

    public PageRank putPageRank(PageRank pageRank) {
        Query query = new Query(Criteria.where("docId").is(pageRank.getDocId()));
        Update update = new Update()
                .set("pageRank", pageRank.getPageRank());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<PageRank> cls = PageRank.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    public List<PageRank> allPageRanks() {
        return mongoTemplate.findAll(PageRank.class);
    }

    public boolean updatePageRank() {
        synchronized (PageRankService.class) {
            List<Document> documents = mongoTemplate.findAll(Document.class);
            Map<String, Pair<Double, Integer>> pageRankStatsMap = new HashMap<>();     // docId, (page rank, out degree)
            for (Document document : documents) {
                String docId = document.getDocId();
                double pageRank = 1 / (double) documents.size();
                Set<String> childrenUrls = document.getChildrenUrls();
                if (childrenUrls == null) {
                    logger.warn("Children urls not found: " + document.getUrl());
                    childrenUrls = new HashSet<>();
                }
                int outDegree = childrenUrls.size();
                Pair<Double, Integer> pageRankStat = Pair.of(pageRank, outDegree);

                pageRankStatsMap.put(docId, pageRankStat);
            }

            Map<String, Set<String>> parentDocIdsMap = new HashMap<>();     // docId, parentDocIds
            for (Document document : documents) {
                String docId = document.getDocId();

                Set<String> childrenUrls = document.getChildrenUrls();
                if (childrenUrls == null) {
                    logger.warn("Children urls not found: " + document.getUrl());
                    childrenUrls = new HashSet<>();
                }

                for (String childUrl : childrenUrls) {
                    Document childDocument = mongoTemplate.findOne(new Query(Criteria.where("url").is(childUrl)), Document.class);
                    if (childDocument == null) {
//                        logger.warn("Child document not found: " + childUrl);
                        continue;
                    }
                    String childDocId = childDocument.getDocId();

                    Set<String> parentDocIds = parentDocIdsMap.get(childDocId);
                    if (parentDocIds == null)
                        parentDocIds = new HashSet<>();
                    parentDocIds.add(docId);

                    parentDocIdsMap.put(childDocId, parentDocIds);
                }
            }

            BiFunction<Map<String, Pair<Double, Integer>>, Map<String, Pair<Double, Integer>>, Boolean> checkCanStop = (pageRankStatMap1, pageRankStatMap2) -> {
                double l2Norm = 0;
                double stopThreshold = 0.00001;
                for (Map.Entry<String, Pair<Double, Integer>> page : pageRankStatMap1.entrySet()) {
                    String docId = page.getKey();
                    Pair<Double, Integer> oldPageRankStat = page.getValue();
                    Pair<Double, Integer> newPageRankStat = pageRankStatMap2.get(docId);
                    if (oldPageRankStat == null || newPageRankStat == null) {
                        logger.warn("page rank stat is null");
                        continue;
                    }
                    double oldPageRank = oldPageRankStat.getFirst();
                    double newPageRank = newPageRankStat.getFirst();
                    l2Norm += Math.pow(newPageRank - oldPageRank, 2);
                }

                return l2Norm < stopThreshold;
            };

            double d = 0.85;
            // Synchronize update
            Map<String, Pair<Double, Integer>> newPageRankStatMap;
            while (true) {
                newPageRankStatMap = new HashMap<>();
                for (Map.Entry<String, Pair<Double, Integer>> page : pageRankStatsMap.entrySet()) {
                    String docId = page.getKey();
                    double newPageRank = 0;
                    Integer outDegree = page.getValue().getSecond();

                    Set<String> parentDocIds = parentDocIdsMap.get(docId);
                    if (parentDocIds == null) {
                        logger.warn("Parent doc ids is null: " + docId);
                        continue;
                    }
                    if (parentDocIds.isEmpty()) {
                        logger.warn("Parent doc ids is empty: " + docId);
                        newPageRank = 1 / (double) pageRankStatsMap.size();
                        newPageRankStatMap.put(docId, Pair.of(newPageRank, outDegree));
                        continue;
                    }
                    for (String parentDocId : parentDocIds) {
                        Pair<Double, Integer> parentPageRankStat = pageRankStatsMap.get(parentDocId);
                        if (parentPageRankStat == null) {
                            logger.warn("Parent page rank stat is null");
                            continue;
                        }
                        if (parentPageRankStat.getSecond() == 0) {
                            logger.warn("Parent page out degree is 0");
                            continue;
                        }

                        if (parentPageRankStat.getFirst() / parentPageRankStat.getSecond() > 1)
                            logger.warn("Ratio greater than 1: " + parentPageRankStat.getFirst() / parentPageRankStat.getSecond());
                        newPageRank += parentPageRankStat.getFirst() / parentPageRankStat.getSecond();
                    }

                    newPageRank = (1 - d) + d * newPageRank;
                    newPageRankStatMap.put(docId, Pair.of(newPageRank, outDegree));
                }

                boolean canStop = checkCanStop.apply(pageRankStatsMap, newPageRankStatMap);
                pageRankStatsMap = newPageRankStatMap;

                if (canStop)
                    break;
            }

            // update page rank
            List<PageRank> pageRanks = new LinkedList<>();
            double maxPageRank = 0;
            for (Map.Entry<String, Pair<Double, Integer>> page : pageRankStatsMap.entrySet()) {
                String docId = page.getKey();
                double newPageRank = page.getValue().getFirst();
                PageRank pageRank = new PageRank(docId, newPageRank);
                pageRanks.add(pageRank);
                maxPageRank = Math.max(maxPageRank, newPageRank);
            }

            if (maxPageRank == 0) {
                logger.warn("Max page rank is 0");
                return false;
            }

            for (PageRank pageRank : pageRanks) {
                double pag = pageRank.getPageRank();
                pageRank.setPageRank(pag / maxPageRank);
                putPageRank(pageRank);
            }

            return true;
        }
    }
}
