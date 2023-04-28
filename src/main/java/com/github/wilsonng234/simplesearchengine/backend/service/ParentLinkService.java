package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.repository.ParentLinkRepository;
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

import java.util.Optional;

@Service
@Scope("prototype")
public class ParentLinkService {
    private static final Logger logger = LogManager.getLogger(ParentLinkService.class);
    @Autowired
    ParentLinkRepository parentLinkRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public ParentLink putParentLinks(ParentLink parentLink) {
        Query query = new Query(Criteria.where("url").is(parentLink.getUrl()));
        Update update = new Update().set("url", parentLink.getUrl()).set("parentUrls", parentLink.getParentUrls());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<ParentLink> cls = ParentLink.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }

    public Optional<ParentLink> getParentLinks(String url) {
        return parentLinkRepository.findParentLinkByUrl(url);
    }
}
