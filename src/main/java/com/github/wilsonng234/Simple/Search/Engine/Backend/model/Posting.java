package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Posting {
    private String docId;
    private List<Long> wordPositions;
}
