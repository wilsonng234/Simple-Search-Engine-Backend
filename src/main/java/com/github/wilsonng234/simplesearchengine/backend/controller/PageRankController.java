package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.service.PageRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/pageRanks")
@Scope("prototype")
public class PageRankController {
    @Autowired
    private PageRankService pageRankService;

    @PostMapping
    public ResponseEntity<String> updatePageRank() {
        HttpStatus httpStatus = pageRankService.updatePageRank() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = httpStatus == HttpStatus.OK ? "Update PageRank successful" : "Update PageRank failed";

        return ResponseEntity.status(httpStatus).body(message);
    }
}
