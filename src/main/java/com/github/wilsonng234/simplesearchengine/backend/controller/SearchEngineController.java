package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.service.SearchEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/searchEngine")
public class SearchEngineController {
    @Autowired
    SearchEngineService searchEngineService;

    @GetMapping
    public List<Document> search(@RequestParam String query) {
        List<Document> results = searchEngineService.search(query);
        results.stream().map(Document::getTitle).collect(Collectors.toList()).forEach(System.out::println);

        return results;
    }
}
