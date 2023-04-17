package com.github.wilsonng234.simplesearchengine.backend.util;

import org.springframework.data.util.Pair;

import java.util.Comparator;
import java.util.List;

public class CrawlerUtils {
    public static List<Pair<String, Integer>> sortWordFreqs(List<Pair<String, Integer>> wordFreqs) {
        Comparator<Pair<String, Integer>> comparator = Comparator.comparing(Pair::getSecond);
        return wordFreqs.stream().sorted(comparator.reversed()).toList();
    }
}
