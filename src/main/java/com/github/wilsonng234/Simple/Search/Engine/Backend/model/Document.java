package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {
    @Id
    private String docId;
    @Indexed(unique = true)
    private String url;
    private long size;
    private String pageTitle;
    private String lastModificationDate;
    private List<Pair<String, Integer>> titleWordIDFreqPairs;      // Pair<wordID, frequency>
    private List<Pair<String, Integer>> bodyWordIDFreqPairs;       // Pair<wordID, frequency>
    private List<String> childrenUrls;

    public Document() {
        size = 0;
        pageTitle = null;
        lastModificationDate = null;
        titleWordIDFreqPairs = null;
        bodyWordIDFreqPairs = null;
        childrenUrls = null;
    }
}
