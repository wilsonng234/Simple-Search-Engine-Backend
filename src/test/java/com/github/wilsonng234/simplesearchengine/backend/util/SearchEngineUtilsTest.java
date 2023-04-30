package com.github.wilsonng234.simplesearchengine.backend.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineUtilsTest {

    @Test
    void getTopKIndices() {
        List<Double> scores = List.of(
                -1.95, 4.91, 1.23, 4.26, -1.35,
                -2.53, -1.34, 2.71, 9.74, 1.72,
                6.41, -5.02, 3.78, -2.33, 1.24,
                -1.48, 0.0, 1.06, 1.19, -0.0
        );
        int k = 10;

        List<Integer> topKIndices = SearchEngineUtils.getTopKIndices(scores, k);
        assertEquals(
                List.of(8, 10, 1, 3, 12, 7, 9, 14, 2, 18),
                topKIndices
        );
    }
}