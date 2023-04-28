package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.TitlePostingList;
import com.github.wilsonng234.simplesearchengine.backend.repository.TitlePostingListRepository;
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
public class TitlePostingListService extends PostingListService {
    private static final Logger logger = LogManager.getLogger(TitlePostingListService.class);
    @Autowired
    private TitlePostingListRepository titlePostingListRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PostingService postingService;

    @Override
    public List<? extends PostingList> allPostingLists() {
        return titlePostingListRepository.findAll();
    }

    @Override
    public TitlePostingList createPostingList(String wordId) {
        return titlePostingListRepository.insert(new TitlePostingList(wordId));
    }

    @Override
    public TitlePostingList putPositingList(String wordId, Posting posting) {
        String postingId = posting.getPostingId();
        if (postingId == null)
            postingId = postingService.putPosting(posting).getPostingId();

        Query query = new Query(Criteria.where("wordId").is(wordId));
        Update update = new Update().max("maxTF", posting.getWordPositions().size());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<TitlePostingList> cls = TitlePostingList.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    @Override
    public TitlePostingList getPostingList(String wordId) {
        Optional<TitlePostingList> titlePostingListOptional = titlePostingListRepository.findTitlePostingListByWordId(wordId);
        return titlePostingListOptional.orElseGet(() -> createPostingList(wordId));
    }
}
