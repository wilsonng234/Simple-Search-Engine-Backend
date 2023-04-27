package com.github.wilsonng234.simplesearchengine.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "bodyPostingLists")
public class BodyPostingList extends PostingList {
    public BodyPostingList(String wordId, List<Posting> postings) {
        super(wordId, postings);
    }
}
