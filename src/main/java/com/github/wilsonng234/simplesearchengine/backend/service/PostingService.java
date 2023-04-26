package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class PostingService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Posting putPosting(Posting posting) {
        Query query = new Query(Criteria.where("docId").is(posting.getDocId()).and("wordId").is(posting.getWordId()));
        Update update = new Update()
                .set("wordPositions", posting.getWordPositions());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Posting> cls = Posting.class;

        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
    }

    public Posting putPosting(String wordId, String docId, List<Long> positions) {
        Query query = new Query(Criteria.where("docId").is(docId).and("wordId").is(wordId));
        Update update = new Update()
                .set("wordPositions", positions);
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Posting> cls = Posting.class;

        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
    }
}
