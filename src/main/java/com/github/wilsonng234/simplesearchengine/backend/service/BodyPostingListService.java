package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.BodyPostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.repository.BodyPostingListRepository;
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
public class BodyPostingListService extends PostingListService {
    private static final Logger logger = LogManager.getLogger(BodyPostingListService.class);
    @Autowired
    private BodyPostingListRepository bodyPostingListRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<? extends PostingList> allPostingLists() {
        return bodyPostingListRepository.findAll();
    }

    @Override
    public BodyPostingList createPostingList(String wordId) {
        return bodyPostingListRepository.insert(new BodyPostingList(wordId));
    }

    @Override
    public BodyPostingList putPositingList(String wordId, Posting posting) {
        Query query = new Query(Criteria.where("wordId").is(wordId));
        Update update = new Update().max("maxTF", posting.getTf());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<BodyPostingList> cls = BodyPostingList.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    @Override
    public BodyPostingList getPostingList(String wordId) {
        Optional<BodyPostingList> bodyPostingListOptional = bodyPostingListRepository.findBodyPostingListByWordId(wordId);
        return bodyPostingListOptional.orElseGet(() -> createPostingList(wordId));
    }
}
