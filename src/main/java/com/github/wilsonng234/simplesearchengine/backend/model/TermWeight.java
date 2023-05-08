package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(def = "{'docId': 1, 'wordId': 1}", unique = true)
@Document(collection = "termWeights")
public class TermWeight {

    private String docId;
    private String wordId;
    private double termWeight;

    public TermWeight(String docId, String wordId) {
        this.docId = docId;
        this.wordId = wordId;
        this.termWeight = 0.0;
    }
}
