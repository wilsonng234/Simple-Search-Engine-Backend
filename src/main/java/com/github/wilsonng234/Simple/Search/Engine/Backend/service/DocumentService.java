package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Document;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public Document createDocument(String url, long size, String title, long lastModificationDate,
                                   Map<String, Integer> titleWordIDFreqPairs,
                                   Map<String, Integer> bodyWordIDFreqPairs,
                                   List<String> childrenUrls) {
        Document createdDocument = new Document(url, size, title, lastModificationDate, titleWordIDFreqPairs, bodyWordIDFreqPairs, childrenUrls);

        return createDocument(createdDocument);
    }

}
