package com.github.wilsonng234.simplesearchengine.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "parentLinks")
public class ParentLink {
    @Id
    private String docId;

    @Indexed(unique = true)
    private String url;

    private List<String> parentUrls;

    public ParentLink(String url, List<String> parentUrls) {
        this.url = url;
        this.parentUrls = parentUrls;
    }
}
