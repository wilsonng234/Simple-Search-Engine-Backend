package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.service.SearchEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/searchEngine")
@Scope("prototype")
public class SearchEngineController {
    @Autowired
    SearchEngineService searchEngineService;

    @GetMapping
    public ResponseEntity<List<SearchEngineService.QueryResult>> search(@RequestParam String query) {
        return new ResponseEntity<>(searchEngineService.search(query), HttpStatus.OK);
    }
}
