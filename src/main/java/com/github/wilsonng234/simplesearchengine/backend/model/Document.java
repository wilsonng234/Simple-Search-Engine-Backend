package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Map;
import java.util.Set;

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
    private Map<String, Integer> titleWordIDFreqsMap;      // wordID, frequency
    private Map<String, Integer> bodyWordIDFreqsMap;       // wordID, frequency
    private Set<String> childrenUrls;

    public Document() {
        size = 0;
        title = null;
        lastModificationDate = 0;
        titleWordIDFreqsMap = null;
        bodyWordIDFreqsMap = null;
        childrenUrls = null;
    }

    public Document(String url, long size, String title, long lastModificationDate,
                    Map<String, Integer> titleWordIDFreqsMap,
                    Map<String, Integer> bodyWordIDFreqsMap,
                    Set<String> childrenUrls) {
        this.url = url;
        this.size = size;
        this.title = title;
        this.lastModificationDate = lastModificationDate;
        this.titleWordIDFreqsMap = titleWordIDFreqsMap;
        this.bodyWordIDFreqsMap = bodyWordIDFreqsMap;
        this.childrenUrls = childrenUrls;
    }
}
