package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Word> allWords() {
        return wordRepository.findAll();
    }

    public Word createWord(String word) {
        Word createdWord = new Word(word);

        return wordRepository.insert(createdWord);
    }
}
