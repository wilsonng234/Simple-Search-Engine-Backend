package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "titlePostingLists")
public class TitlePostingList extends PostingList {
    public TitlePostingList(String wordId, List<Posting> postings) {
        super(wordId, postings);
    }
}
