package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
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
public class PostingListService {
    private static final Logger logger = LogManager.getLogger(PostingListService.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    public PostingList putPostingList(String wordId, String type, Posting posting) {
        Query query = new Query(
                Criteria.where("wordId").is(wordId)
                        .and("type").is(type)
        );
        Update update = new Update().max("maxTF", posting.getTf());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<PostingList> cls = PostingList.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    public PostingList getPostingList(String wordId, String type) {
        Query query = new Query(
                Criteria.where("wordId").is(wordId)
                        .and("type").is(type)
        );

        return mongoTemplate.findOne(query, PostingList.class);
    }
}
