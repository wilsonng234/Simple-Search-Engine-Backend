package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pageRanks")
public class PageRank {
    @Indexed
    private String docId;
    private double pageRank;

    public PageRank(String docId) {
        this.docId = docId;
        this.pageRank = 0.0;
    }
}
