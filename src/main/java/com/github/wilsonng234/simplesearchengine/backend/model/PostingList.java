package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(def = "{'wordId': 1, 'type': 1}", unique = true)
@Document(collection = "postingLists")
public class PostingList {
    private String wordId;
    private String type;            // type.equals("title") || type.equals("body");
    private int maxTF = 0;

    public PostingList(String wordId, String type) {
        this.wordId = wordId;
        this.type = type;
    }
}
