package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Word;
import com.mongodb.DuplicateKeyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Scope("prototype")
public class WordService {
    private static final Logger logger = LogManager.getLogger(WordService.class);

    public enum QueryType {
        WORD, WORDID
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param key  -  word or wordId
     * @param type - "word" or "wordId"
     **/
    public Optional<Word> getWord(String key, QueryType type) {
        if (type == QueryType.WORDID) {
            Query query = new Query(Criteria.where("wordId").is(key));
            return Optional.ofNullable(mongoTemplate.findOne(query, Word.class));
        } else if (type == QueryType.WORD) {
            Query query = new Query(Criteria.where("word").is(key));
            return Optional.ofNullable(mongoTemplate.findOne(query, Word.class));
        } else
            return Optional.empty();
    }

    public List<Word> allWords() {
        return mongoTemplate.findAll(Word.class);
    }

    public Word putWord(String word) {
        Query query = new Query(Criteria.where("word").is(word));
        Update update = new Update().set("word", word);
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().upsert(true).returnNew(true);
        Class<Word> cls = Word.class;

        try {
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        } catch (DuplicateKeyException duplicateKeyException) {
            // update again if duplicate key exception
            logger.warn(duplicateKeyException.getMessage());
            return mongoTemplate.findAndModify(query, update, findAndModifyOptions, cls);
        }
    }
}
