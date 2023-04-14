package com.github.wilsonng234.simplesearchengine.backend.repository;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends MongoRepository<Document, String> {
    Optional<Document> findDocumentByDocId(String docId);

    Optional<Document> findDocumentByUrl(String url);
}
