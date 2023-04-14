package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {
    @Id
    private String docId;
    @Indexed(unique = true)
    private String url;
    private long size;
    private String title;
    private long lastModificationDate;
    private Map<String, Integer> titleWordIDFreqPairs;      // wordID, frequency
    private Map<String, Integer> bodyWordIDFreqPairs;       // wordID, frequency
    private List<String> childrenUrls;

    public Document() {
        size = 0;
        title = null;
        lastModificationDate = 0;
        titleWordIDFreqPairs = null;
        bodyWordIDFreqPairs = null;
        childrenUrls = null;
    }

    public Document(String url, long size, String title, long lastModificationDate,
                    Map<String, Integer> titleWordIDFreqPairs,
                    Map<String, Integer> bodyWordIDFreqPairs,
                    List<String> childrenUrls) {
        this.url = url;
        this.size = size;
        this.title = title;
        this.lastModificationDate = lastModificationDate;
        this.titleWordIDFreqPairs = titleWordIDFreqPairs;
        this.bodyWordIDFreqPairs = bodyWordIDFreqPairs;
        this.childrenUrls = childrenUrls;
    }
}
