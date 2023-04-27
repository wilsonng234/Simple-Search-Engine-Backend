package com.github.wilsonng234.simplesearchengine.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "titlePostingLists")
public class TitlePostingList extends PostingList {
    public TitlePostingList(String wordId, List<String> postingIds) {
        super(wordId, postingIds);
    }
}
