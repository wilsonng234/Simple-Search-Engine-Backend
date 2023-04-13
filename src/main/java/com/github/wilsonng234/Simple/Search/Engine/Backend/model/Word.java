package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "words")
public class Word {
    @Id
    private String wordId;
    @Indexed(unique = true)
    private String word;

    public Word(String word) {
        this.word = word;
    }
}
