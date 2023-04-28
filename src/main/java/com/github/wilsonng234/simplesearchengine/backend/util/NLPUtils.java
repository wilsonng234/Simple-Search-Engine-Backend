package com.github.wilsonng234.simplesearchengine.backend.util;

import ca.rmen.porterstemmer.PorterStemmer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class NLPUtils {
    private static final Logger logger = LogManager.getLogger(NLPUtils.class);
    private static final String RESOURCE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    private static final Set<String> stopWords = new HashSet<>();
    private static final PorterStemmer porterStemmer = new PorterStemmer();
    private static ThreadLocal<StringBuilder> stringBuilderThreadLocal = ThreadLocal.withInitial(StringBuilder::new);

    static {
        try {
            FileReader fileReader = new FileReader(RESOURCE_PATH + File.separator + "static" + File.separator + "stopwords.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String stopWord = bufferedReader.readLine();
            while (stopWord != null) {
                stopWords.add(stopWord.toLowerCase());
                stopWord = bufferedReader.readLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static List<String> tokenize(String text) {
        List<String> words = new LinkedList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        while (stringTokenizer.hasMoreTokens()) {
            words.add(stringTokenizer.nextToken());
        }

        return words;
    }

    public static List<String> removeStopWords(List<String> words) {
        return words.stream().filter(word -> !stopWords.contains(word.toLowerCase())).collect(Collectors.toCollection(LinkedList::new));
    }

    public static String stemWord(String word) {
        return porterStemmer.stemWord(word);
    }

    public static List<String> stemWords(List<String> words) {
        return words.stream().map(porterStemmer::stemWord).collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<String> nGrams(List<String> words, int n) {
        List<String> nGrams = new LinkedList<>();
        if (words.size() == 0)
            return nGrams;

        StringBuilder stringBuilder = stringBuilderThreadLocal.get();
        for (int i = 0; i < words.size() - n + 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                stringBuilder.append(words.get(i + j));
                stringBuilder.append(" ");
            }
            stringBuilder.append(words.get(i + n - 1));

            nGrams.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }

        System.out.println(nGrams);
        return nGrams;
    }

    public static List<String> parsePhraseSearchQuery(String query) {
        List<Integer> quotationMarksIndices = new LinkedList<>();
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '"')
                quotationMarksIndices.add(i);
        }

        if (quotationMarksIndices.size() % 2 != 0) {
            logger.warn("Invalid phrase search query: " + query);
        }

        List<String> phrases = new LinkedList<>();
        for (int i = 0; i + 1 < quotationMarksIndices.size(); i += 2) {
            String phrase = query.substring(quotationMarksIndices.get(i) + 1, quotationMarksIndices.get(i + 1));
            phrase = phrase.strip();
            phrases.add(phrase);
        }

        return phrases;
    }
}
