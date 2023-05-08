package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "termWeights")
public class TermWeight {
    @Id
    private String docId;
    private Map<String, Double> termWeights;     // non-zero term weights   (key: wordId, value: term weight)
}
