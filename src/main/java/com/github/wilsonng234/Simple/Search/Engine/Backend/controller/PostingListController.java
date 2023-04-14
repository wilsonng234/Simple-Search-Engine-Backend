package com.github.wilsonng234.Simple.Search.Engine.Backend.controller;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Posting;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.PostingList;
import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class PostingListController {
    @GetMapping
    public abstract ResponseEntity<List<? extends PostingList>> getAllPostingLists();

    @PostMapping
    public abstract ResponseEntity<? extends PostingList> createPostingList(@RequestBody Word word);

    @PutMapping("/{wordId}")
    public abstract ResponseEntity<? extends PostingList> putPostingList(@PathVariable String wordId, @RequestBody Posting posting);
}
