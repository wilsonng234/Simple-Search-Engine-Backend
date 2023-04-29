package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/words")
@Scope("prototype")
public class WordController {
    @Autowired
    private WordService wordService;

    @GetMapping
    public ResponseEntity<Optional<Word>> getWord(@RequestParam Optional<String> word, @RequestParam Optional<String> wordId) {
        if (word.isPresent() && wordId.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (word.isPresent())
            return new ResponseEntity<>(wordService.getWord(word.get(), WordService.QueryType.WORD), HttpStatus.OK);
        else if (wordId.isPresent())
            return new ResponseEntity<>(wordService.getWord(wordId.get(), WordService.QueryType.WORDID), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<Word> putWord(@RequestBody Word word) {
        return new ResponseEntity<>(wordService.putWord(word.getWord()), HttpStatus.CREATED);
    }
}
