package com.github.wilsonng234.simplesearchengine.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "titlePostingLists")
public class TitlePostingList extends PostingList {
    public TitlePostingList(String wordId) {
        super(wordId);
    }
}
