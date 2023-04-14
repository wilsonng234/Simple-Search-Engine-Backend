package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.BodyPostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import com.github.wilsonng234.Simple.Search.Engine.Backend.service.BodyPostingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/bodyPostingLists")
public class BodyPostingListController extends PostingListController {
    @Autowired
    private BodyPostingListService bodyPostingListService;

    @Override
    public ResponseEntity<List<? extends PostingList>> getAllPostingLists() {
        return new ResponseEntity<>(bodyPostingListService.allPostingLists(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<? extends PostingList> createPostingList(Word word) {
        String wordId = word.getWordId();

        return new ResponseEntity<>(bodyPostingListService.createPostingList(wordId), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<? extends PostingList> putPostingList(String wordId, Posting posting) {
        return new ResponseEntity<>(bodyPostingListService.putPositingList(wordId, posting), HttpStatus.OK);
    }
}
