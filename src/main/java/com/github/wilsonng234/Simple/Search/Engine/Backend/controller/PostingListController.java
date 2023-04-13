package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.service.PostingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/postingLists")
public class PostingListController {
    @Autowired
    private PostingListService postingListService;

    @GetMapping
    public ResponseEntity<List<PostingList>> getAllMovies() {
        return new ResponseEntity<>(postingListService.allPostingLists(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostingList> createPostingList(@RequestBody Map<String, String> payload) {
        String wordId = payload.get("wordId");

        return new ResponseEntity<>(postingListService.createPostingList(wordId), HttpStatus.CREATED);
    }

    @PutMapping("/{wordId}")
    public ResponseEntity<PostingList> putPostingList(@PathVariable String wordId, @RequestBody Posting posting) {
        System.out.println(posting);
        return new ResponseEntity<>(postingListService.putPositingList(wordId, posting), HttpStatus.CREATED);
    }
}