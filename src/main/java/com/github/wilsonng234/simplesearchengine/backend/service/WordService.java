package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.github.wilsonng234.simplesearchengine.backend.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    public enum QueryType {
        WORD, WORDID
    }

    @Autowired
    private WordRepository wordRepository;

    /**
     * @param key  -  word or wordId
     * @param type - "word" or "wordId"
     **/
    public Optional<Word> getWord(String key, QueryType type) {
        if (type == QueryType.WORD)
            return wordRepository.findWordByWord(key);
        else if (type == QueryType.WORDID)
            return wordRepository.findWordByWordId(key);
        else
            return Optional.empty();
    }

    public List<Word> allWords() {
        return wordRepository.findAll();
    }

    public Word createWord(String word) {
        Word createdWord = new Word(word);

        return wordRepository.insert(createdWord);
    }
}
