package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "postingLists")
public class PostingList {
    @Id
    private String wordId;
    private List<Posting> postingList;
}