package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
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
public class DocumentService {
    private static final Logger logger = LogManager.getLogger(DocumentService.class);

    public enum QueryType {
        URL, DOCID
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param key  -  url or docId
     * @param type - "url" or "docId"
     **/
    public Optional<Document> getDocument(String key, QueryType type) {
        if (type == QueryType.URL) {
            Query query = new Query(Criteria.where("url").is(key));
            return Optional.ofNullable(mongoTemplate.findOne(query, Document.class));
        } else if (type == QueryType.DOCID) {
            Query query = new Query(Criteria.where("docId").is(key));
            return Optional.ofNullable(mongoTemplate.findOne(query, Document.class));
        } else
            return Optional.empty();
    }

    public List<Document> allDocuments() {
        return mongoTemplate.findAll(Document.class);
    }

    public Document putDocument(Document document) {
        Query query = new Query(Criteria.where("url").is(document.getUrl()));
        Update update = new Update().set("url", document.getUrl())
                .set("size", document.getSize())
                .set("title", document.getTitle())
                .set("lastModificationDate", document.getLastModificationDate())
                .set("titleWordFreqs", document.getTitleWordFreqs())
                .set("bodyWordFreqs", document.getBodyWordFreqs())
                .set("childrenUrls", document.getChildrenUrls())
                .set("maxTF", document.getMaxTF());
        FindAndModifyOptions findAndModifyOptions = org.springframework.data.mongodb.core.FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Document> cls = Document.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }
}
