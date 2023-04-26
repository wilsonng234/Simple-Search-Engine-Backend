package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.repository.ParentLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Scope("prototype")
public class ParentLinkService {
    @Autowired
    ParentLinkRepository parentLinkRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public ParentLink createParentLinks(ParentLink parentLink) {
        Set<String> parentLinks = parentLink.getParentUrls() == null ? new HashSet<>() : parentLink.getParentUrls();

        return parentLinkRepository.insert(new ParentLink(parentLink.getUrl(), parentLinks));
    }

    public ParentLink putParentLinks(ParentLink parentLink) {
        Query query = new Query(Criteria.where("url").is(parentLink.getUrl()));
        Update update = new Update().set("url", parentLink.getUrl()).set("parentUrls", parentLink.getParentUrls());
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<ParentLink> cls = ParentLink.class;

        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
    }

    public Optional<ParentLink> getParentLinks(String url) {
        return parentLinkRepository.findParentLinkByUrl(url);
    }

    public List<ParentLink> allParentLinks() {
        return parentLinkRepository.findAll();
    }
}
