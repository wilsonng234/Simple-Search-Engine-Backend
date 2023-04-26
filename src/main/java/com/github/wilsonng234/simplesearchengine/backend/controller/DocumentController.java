package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/documents")
@Scope("prototype")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @GetMapping
    public ResponseEntity<Optional<Document>> getDocument(@RequestParam Optional<String> url, @RequestParam Optional<String> docId) {
        if (url.isPresent() && docId.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (url.isPresent())
            return new ResponseEntity<>(documentService.getDocument(url.get(), DocumentService.QueryType.URL), HttpStatus.OK);
        else if (docId.isPresent())
            return new ResponseEntity<>(documentService.getDocument(docId.get(), DocumentService.QueryType.DOCID), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return new ResponseEntity<>(documentService.allDocuments(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Document> putDocument(@RequestBody Document document) {
        return new ResponseEntity<>(documentService.putDocument(document), HttpStatus.OK);
    }
}
