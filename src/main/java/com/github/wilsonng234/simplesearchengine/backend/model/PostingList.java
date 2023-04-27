package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostingList {
    @Id
    private String wordId;
    @DocumentReference
    private List<Posting> postings;
    private int maxTF = 0;

    public PostingList(String wordId, List<Posting> postings) {
        this.wordId = wordId;
        this.postings = postings;
    }
}
