package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.model.PostingList;
import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.service.TitlePostingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/titlePostingLists")
public class TitlePostingListController extends PostingListController {
    @Autowired
    private TitlePostingListService titlePostingListService;

    @Override
    @GetMapping("{wordId}")
    public ResponseEntity<? extends PostingList> getPostingList(@PathVariable String wordId) {
        return new ResponseEntity<>(titlePostingListService.getPostingList(wordId), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<? extends PostingList>> getAllPostingLists() {
        return new ResponseEntity<>(titlePostingListService.allPostingLists(), HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<? extends PostingList> createPostingList(@RequestBody Word word) {
        String wordId = word.getWordId();

        return new ResponseEntity<>(titlePostingListService.createPostingList(wordId), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{wordId}")
    public ResponseEntity<? extends PostingList> putPostingList(@PathVariable String wordId, @RequestBody Posting posting) {
        return new ResponseEntity<>(titlePostingListService.putPositingList(wordId, posting), HttpStatus.OK);
    }
}
