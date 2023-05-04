package com.github.wilsonng234.simplesearchengine.backend.service;

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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
public class PageRankService {
    private static final Logger logger = LogManager.getLogger(PageRankService.class);
    @Autowired
    MongoTemplate mongoTemplate;

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

    public void updatePageRank() {
        // TODO: implement update page rank
    }
}
