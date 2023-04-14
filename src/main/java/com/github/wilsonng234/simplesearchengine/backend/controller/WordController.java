package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/words")
public class WordController {
    @Autowired
    private WordService wordService;

    @GetMapping
    public ResponseEntity<List<Word>> getAllWords() {
        return new ResponseEntity<>(wordService.allWords(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Word> createWord(@RequestBody Word word) {
        return new ResponseEntity<>(wordService.createWord(word.getWord()), HttpStatus.CREATED);
    }
}
