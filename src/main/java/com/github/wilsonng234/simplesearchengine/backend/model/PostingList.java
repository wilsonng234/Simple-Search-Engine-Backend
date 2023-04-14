package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
public abstract class PostingList {
    @Id
    private String wordId;
    private List<Posting> postings;
    private int maxTF = 0;

    public PostingList(String wordId, List<Posting> postings) {
        this.wordId = wordId;
        this.postings = postings;
    }
}
