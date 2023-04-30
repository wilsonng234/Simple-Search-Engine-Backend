package com.github.wilsonng234.simplesearchengine.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class VSMUtilsTest {
    private static final double DELTA = 0.000001;
    private static final List<Double> vector1;
    private static final List<Double> vector2;

    static {
        vector1 = List.of(1.0, 2.0, 3.0);
        vector2 = List.of(4.0, 5.0, 6.0);
    }

    @Test
    void getMagnitude() {
        Assertions.assertEquals(3.741657387, VSMUtils.getMagnitude(vector1), DELTA);
        Assertions.assertEquals(8.774964387, VSMUtils.getMagnitude(vector2), DELTA);
    }

    @Test
    void getDotProduct() {
        Assertions.assertEquals(32.0, VSMUtils.getDotProduct(vector1, vector2), DELTA);
    }

    @Test
    void getIDF() {
        int numPages = 10;
        int documentFrequency = 3;

        System.out.println(VSMUtils.getIDF(numPages, documentFrequency));
        Assertions.assertEquals(1.736965594, VSMUtils.getIDF(numPages, documentFrequency), DELTA);
    }

    @Test
    void getTermWeight() {
        int termFrequency = 2;
        int numDocuments = 10;
        int documentFrequency = 3;
        int maxTermFrequency = 10;

        Assertions.assertEquals(
                0.347393119,
                VSMUtils.getTermWeight(termFrequency, numDocuments, documentFrequency, maxTermFrequency),
                DELTA)
        ;
    }

    @Test
    void getCosineSimilarity() {
        Assertions.assertEquals(0.97463185, VSMUtils.getCosineSimilarity(vector1, vector2), DELTA);
    }
}