package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostingList {
    @Id
    private String wordId;
    private List<String> postingIds;
    private int maxTF = 0;

    public PostingList(String wordId, List<String> postingIds) {
        this.wordId = wordId;
        this.postingIds = postingIds;
    }
}
