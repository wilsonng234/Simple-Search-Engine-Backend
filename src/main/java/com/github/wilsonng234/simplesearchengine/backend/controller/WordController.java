package com.github.wilsonng234.simplesearchengine.backend.controller;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/words")
@Scope("prototype")
public class WordController {
    @Autowired
    private WordService wordService;

    @GetMapping
    public ResponseEntity<Word> getWord(@RequestParam Optional<String> wordId, @RequestParam Optional<String> word) {
        if (wordId.isPresent() && word.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (wordId.isPresent()) {
            Optional<Word> wordObj = wordService.getWord(wordId.get(), WordService.QueryType.WORDID);

            if (wordObj.isPresent())
                return new ResponseEntity<>(wordObj.get(), HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (word.isPresent()) {
            Optional<Word> wordObj = wordService.getWord(word.get(), WordService.QueryType.WORD);

            if (wordObj.isPresent())
                return new ResponseEntity<>(wordObj.get(), HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/prefix")
    public ResponseEntity<List<Word>> getWordByPrefix(@RequestParam String prefix) {
        return new ResponseEntity<>(wordService.getWordByPrefix(prefix), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Word> putWord(@RequestBody Word word) {
        return new ResponseEntity<>(wordService.putWord(word.getWord()), HttpStatus.OK);
    }
}
