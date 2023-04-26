package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "posting")
@CompoundIndex(def = "{'wordId': 1, 'docId': 1}", unique = true)
public class Posting {
    @Id
    private String postingId;
    private String wordId;
    private String docId;
    private List<Long> wordPositions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        return postingId.equals(posting.postingId);
    }

}
