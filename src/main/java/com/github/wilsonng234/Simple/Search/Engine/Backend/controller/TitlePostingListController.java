package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import com.github.wilsonng234.Simple.Search.Engine.Backend.service.TitlePostingListService;
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
