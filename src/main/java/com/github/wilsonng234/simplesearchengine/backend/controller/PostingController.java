package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.service.PostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/postings")
@Scope("prototype")
public class PostingController {
    @Autowired
    private PostingService postingService;

    @PutMapping
    public ResponseEntity<Posting> putPosting(@RequestBody Posting posting) {
        return new ResponseEntity<>(postingService.putPosting(posting), HttpStatus.OK);
    }
}
