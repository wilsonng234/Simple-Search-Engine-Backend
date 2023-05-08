package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "postings")
@CompoundIndexes({
        @CompoundIndex(def = "{'type': 1, 'wordId': 1}"),
        @CompoundIndex(def = "{'type': 1, 'wordId': 1, 'docId': 1}", unique = true)
})

public class Posting {
    @Id
    private String postingId;
    private String type;        // type.equals("title") || type.equals("body");
    private String wordId;
    private String docId;
    private int tf;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        return postingId.equals(posting.postingId);
    }

}
