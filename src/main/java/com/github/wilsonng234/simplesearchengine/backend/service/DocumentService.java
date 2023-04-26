package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.repository.DocumentRepository;
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

    public enum QueryType {
        URL, DOCID
    }

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param key  -  url or docId
     * @param type - "url" or "docId"
     **/
    public Optional<Document> getDocument(String key, QueryType type) {
        if (type == QueryType.URL)
            return documentRepository.findDocumentByUrl(key);
        else if (type == QueryType.DOCID)
            return documentRepository.findDocumentByDocId(key);
        else
            return Optional.empty();
    }

    public List<Document> allDocuments() {
        return documentRepository.findAll();
    }

    public Document putDocument(Document document) {
        Query query = new Query(Criteria.where("url").is(document.getUrl()));
        Update update = new Update().set("url", document.getUrl())
                .set("size", document.getSize())
                .set("title", document.getTitle())
                .set("lastModificationDate", document.getLastModificationDate())
                .set("titleWordFreqs", document.getTitleWordFreqs())
                .set("bodyWordFreqs", document.getBodyWordFreqs())
                .set("childrenUrls", document.getChildrenUrls());
        FindAndModifyOptions findAndModifyOptions = org.springframework.data.mongodb.core.FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Document> cls = Document.class;

        return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
    }
}
