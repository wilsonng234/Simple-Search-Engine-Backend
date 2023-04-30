package com.github.wilsonng234.simplesearchengine.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerUtilsTest {

    @Test
    void sortWordFreqs() {
        List<Pair<String, Integer>> wordFreqs = List.of(
                Pair.of("a", 3),
                Pair.of("b", 1),
                Pair.of("c", 4),
                Pair.of("d", 8)
        );

        assertArrayEquals(
                new Pair[]{
                        Pair.of("d", 8),
                        Pair.of("c", 4),
                        Pair.of("a", 3),
                        Pair.of("b", 1),
                },
                CrawlerUtils.sortWordFreqs(wordFreqs).toArray()
        );
    }
}