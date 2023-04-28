package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
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

@Service
@Scope("prototype")
public class PostingService {
    private static final Logger logger = LogManager.getLogger(PostingService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    public Posting putPosting(Posting posting) {
        Query query = new Query(
                Criteria.where("type").is(posting.getType())
                        .and("docId").is(posting.getDocId())
                        .and("wordId").is(posting.getWordId()));
        Update update = new Update().set("tf", posting.getTf());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Posting> cls = Posting.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.info(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    public Posting putPosting(String wordId, String type, String docId, int tf) {
        Query query = new Query(
                Criteria.where("type").is(type)
                        .and("docId").is(docId)
                        .and("wordId").is(wordId));
        Update update = new Update()
                .set("tf", tf);
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Posting> cls = Posting.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn("duplicateKeyException");
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }
}
