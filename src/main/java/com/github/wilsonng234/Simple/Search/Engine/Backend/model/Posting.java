package com.github.wilsonng234.Simple.Search.Engine.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Posting {
    private String docId;
    private List<Long> wordPositions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        return docId.equals(posting.docId);
    }

}
