package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Document;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    public enum QueryType {
        URL, DOCID
    }

    @Autowired
    private DocumentRepository documentRepository;

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

    public Document createDocument(Document document) {
        return documentRepository.insert(document);
    }
}
