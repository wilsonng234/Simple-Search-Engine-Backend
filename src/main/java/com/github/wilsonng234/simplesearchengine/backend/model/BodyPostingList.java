package com.github.wilsonng234.simplesearchengine.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bodyPostingLists")
public class BodyPostingList extends PostingList {
    public BodyPostingList(String wordId) {
        super(wordId);
    }
}
