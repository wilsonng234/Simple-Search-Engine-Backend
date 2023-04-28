package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostingList {
    @Id
    private String wordId;
    private int maxTF = 0;

    public PostingList(String wordId) {
        this.wordId = wordId;
    }
}
