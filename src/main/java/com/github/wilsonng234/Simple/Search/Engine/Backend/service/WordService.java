package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.model.Word;
import com.github.wilsonng234.Simple.Search.Engine.Backend.repository.WordRepository;
import jakarta.annotation.PostConstruct;
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
        wordRepository.insert(createdWord);
        
        return createdWord;
    }
}
