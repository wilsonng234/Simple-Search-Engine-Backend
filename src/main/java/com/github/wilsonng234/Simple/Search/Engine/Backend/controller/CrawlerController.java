package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/crawler")
public class CrawlerController {
    @Autowired
    private CrawlerService crawlerService;

    @PostMapping
    public ResponseEntity<String> crawlWebsite(@RequestParam String url, @RequestParam String pages) {
        HttpStatus httpStatus = crawlerService.crawl(url, pages) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = httpStatus == HttpStatus.OK ? "Crawling successful" : "Crawling failed";

        return ResponseEntity.status(httpStatus).body(message);
    }
}
