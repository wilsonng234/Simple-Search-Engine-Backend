package com.github.wilsonng234.simplesearchengine.backend.util;

import java.util.List;

public class VSMUtils {
    public static double getMagnitude(List<Double> vector) {
        double magnitude = 0.0;

        for (double value : vector) {
            magnitude += value * value;
        }

        return Math.sqrt(magnitude);
    }

    public static double getDotProduct(List<Double> vector1, List<Double> vector2) {
        assert vector1.size() == vector2.size();
        double dotProduct = 0.0;

        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
        }

        return dotProduct;
    }

    public static double getIDF(int numPages, int documentFrequency) {
        return Math.log(numPages / (double) documentFrequency);
    }

    public static double getTermWeight(int termFrequency, int numDocuments, int documentFrequency, int maxTermFrequency) {
        double idf = getIDF(numDocuments, documentFrequency);

        return termFrequency * idf / maxTermFrequency;
    }

    public static double getCosineSimilarity(List<Double> vector1, List<Double> vector2) {
        double dotProduct = getDotProduct(vector1, vector2);
        double magnitude1 = getMagnitude(vector1);
        double magnitude2 = getMagnitude(vector2);

        return dotProduct / (magnitude1 * magnitude2);
    }
}
