package com.github.wilsonng234.simplesearchengine.backend.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SearchEngineUtils {
    private static final Logger logger = LogManager.getLogger(SearchEngineUtils.class);

    public static List<Integer> getTopKIndices(List<Double> scores, int k) {
        if (scores.size() < 1) {
            logger.info("Scores size is 0");
            return new LinkedList<>();
        }

        Comparator<Map.Entry<Integer, Double>> comparator = Map.Entry.comparingByValue();
        PriorityQueue<Map.Entry<Integer, Double>> heap = new PriorityQueue<>(scores.size(), comparator.reversed());

        for (int i = 0; i < scores.size(); i++)
            heap.add(new AbstractMap.SimpleEntry<>(i, scores.get(i)));

        List<Integer> topKIndices = new LinkedList<>();
        for (int i = 0; i < k && !heap.isEmpty(); i++)
            topKIndices.add(heap.poll().getKey());

        return topKIndices;
    }
}
