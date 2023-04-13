package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Document;
import com.github.wilsonng234.Simple.Search.Engine.Backend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return new ResponseEntity<>(documentService.allDocuments(), HttpStatus.OK);
    }
}
