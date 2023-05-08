package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.util.Pair;

import java.util.List;
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
    private List<Pair<String, Integer>> titleWordFreqs;      // word, frequency
    private List<Pair<String, Integer>> bodyWordFreqs;       // word, frequency
    private Set<String> childrenUrls;
    private int titleMaxTF;
    private int bodyMaxTF;

    public Document() {
        size = 0;
        title = null;
        lastModificationDate = 0;
        titleWordFreqs = null;
        bodyWordFreqs = null;
        childrenUrls = null;
        titleMaxTF = 0;
        bodyMaxTF = 0;
    }

    public Document(String url, long size, String title, long lastModificationDate,
                    List<Pair<String, Integer>> titleWordFreqs,
                    List<Pair<String, Integer>> bodyWordFreqs,
                    Set<String> childrenUrls, int titleMaxTF, int bodyMaxTF) {
        this.url = url;
        this.size = size;
        this.title = title;
        this.lastModificationDate = lastModificationDate;
        this.titleWordFreqs = titleWordFreqs;
        this.bodyWordFreqs = bodyWordFreqs;
        this.childrenUrls = childrenUrls;
        this.titleMaxTF = titleMaxTF;
        this.bodyMaxTF = bodyMaxTF;
    }
}
