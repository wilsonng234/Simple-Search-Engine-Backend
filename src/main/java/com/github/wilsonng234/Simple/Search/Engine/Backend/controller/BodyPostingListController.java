package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import com.github.wilsonng234.Simple.Search.Engine.Backend.service.BodyPostingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/bodyPostingLists")
public class BodyPostingListController extends PostingListController {
    @Autowired
    private BodyPostingListService bodyPostingListService;

    @Override
    @GetMapping
    public ResponseEntity<List<? extends PostingList>> getAllPostingLists() {
        return new ResponseEntity<>(bodyPostingListService.allPostingLists(), HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<? extends PostingList> createPostingList(@RequestBody Word word) {
        String wordId = word.getWordId();

        return new ResponseEntity<>(bodyPostingListService.createPostingList(wordId), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{wordId}")
    public ResponseEntity<? extends PostingList> putPostingList(@PathVariable String wordId, @RequestBody Posting posting) {
        return new ResponseEntity<>(bodyPostingListService.putPositingList(wordId, posting), HttpStatus.OK);
    }
}
