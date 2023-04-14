package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Document;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> allDocuments() {
        return documentRepository.findAll();
    }

    public Document createDocument(Document document) {
        return documentRepository.insert(document);
    }
}
