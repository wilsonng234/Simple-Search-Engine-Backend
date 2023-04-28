package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "postingLists")
public class PostingList {
    @Id
    private String wordId;
    private int maxTF = 0;

    public PostingList(String wordId) {
        this.wordId = wordId;
    }
}
