package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "pageRanks")
public class PageRank {
    @Id
    private String docId;
    private double pageRank;

    public PageRank(String docId) {
        this.docId = docId;
        this.pageRank = 1.0;
    }
}
