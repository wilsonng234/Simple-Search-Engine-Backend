package com.github.wilsonng234.simplesearchengine.backend.util;

import ca.rmen.porterstemmer.PorterStemmer;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;

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
    private static final ThreadLocal<StringBuilder> stringBuilderThreadLocal = ThreadLocal.withInitial(StringBuilder::new);

    private static final Set<List<String>> biGramGrammaticalPatterns;
    private static final Set<List<String>> triGramGrammaticalPatterns;

    static {
        try {
            FileReader fileReader = new FileReader(RESOURCE_PATH + File.separator + "static" + File.separator + "stopwords.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String stopWord = bufferedReader.readLine();
            while (stopWord != null) {
                stopWords.add(porterStemmer.stemWord(stopWord));
                stopWord = bufferedReader.readLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // JJ: adjective
        // NN: noun
        // IN: preposition or subordinating conjunction
        biGramGrammaticalPatterns = new HashSet<>();
        biGramGrammaticalPatterns.add(Arrays.asList("JJ", "NN"));
        biGramGrammaticalPatterns.add(Arrays.asList("NN", "NN"));

        triGramGrammaticalPatterns = new HashSet<>();
        triGramGrammaticalPatterns.add(Arrays.asList("JJ", "JJ", "NN"));
        triGramGrammaticalPatterns.add(Arrays.asList("JJ", "NN", "NN"));
        triGramGrammaticalPatterns.add(Arrays.asList("NN", "JJ", "NN"));
        triGramGrammaticalPatterns.add(Arrays.asList("NN", "NN", "NN"));
        triGramGrammaticalPatterns.add(Arrays.asList("NN", "IN", "NN"));
    }

    public static List<Pair<String, String>> partsOfSpeech(String text) {
        Document document = new Document(text);
        List<Sentence> sentences = document.sentences();
        List<String> words = sentences.stream()
                .flatMap(sentence -> sentence.words().stream())
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> posTags = sentences.stream()
                .flatMap(sentence -> sentence.posTags().stream())
                .collect(Collectors.toCollection(ArrayList::new));

        List<Pair<String, String>> partsOfSpeech = new LinkedList<>();
        for (int i = 0; i < words.size() && i < posTags.size(); i++)
            partsOfSpeech.add(Pair.of(words.get(i), posTags.get(i)));

        return partsOfSpeech;
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
        return words.stream()
                .filter(word -> !stopWords.contains(word.toLowerCase()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<Pair<String, String>> removeStopWordPosPairs(List<Pair<String, String>> wordPosPairs) {
        return wordPosPairs.stream()
                .filter(wordPos -> !stopWords.contains(wordPos.getFirst().toLowerCase()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<String> removePunctuations(List<String> words, boolean removeDoubleQuotationMarks) {
        List<String> result = new LinkedList<>();

        String regex = removeDoubleQuotationMarks ? "^[^a-zA-Z0-9]*|[^a-zA-Z0-9]*$"
                : "^[^a-zA-Z0-9\"]*|[^a-zA-Z0-9\"]*$";
        for (String word : words) {
            String punctuationRemovedWord = word.replaceAll(regex, "");

            if (!punctuationRemovedWord.isEmpty())
                result.add(punctuationRemovedWord);
        }

        return result;
    }

    public static List<Pair<String, String>> removePunctuationsWordPosPairs(List<Pair<String, String>> wordPosPairs,
                                                                            boolean removeDoubleQuotationMarks) {
        List<Pair<String, String>> result = new LinkedList<>();

        String regex = removeDoubleQuotationMarks ? "^[^a-zA-Z0-9]*|[^a-zA-Z0-9]*$"
                : "^[^a-zA-Z0-9\"]*|[^a-zA-Z0-9\"]*$";
        for (Pair<String, String> wordPosPair : wordPosPairs) {
            String word = wordPosPair.getFirst();
            String pos = wordPosPair.getSecond();
            String punctuationRemovedWord = word.replaceAll(regex, "");

            if (!punctuationRemovedWord.isEmpty())
                result.add(Pair.of(punctuationRemovedWord, pos));
        }

        return result;
    }

    public static String stemWord(String word) {
        return porterStemmer.stemWord(word);
    }

    public static List<String> stemWords(List<String> words) {
        return words.stream()
                .map(porterStemmer::stemWord)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<Pair<String, String>> stemWordPosPairs(List<Pair<String, String>> wordPosPairs) {
        List<Pair<String, String>> result = new LinkedList<>();

        for (Pair<String, String> wordPosPair : wordPosPairs) {
            String word = wordPosPair.getFirst();
            String pos = wordPosPair.getSecond();
            String stemmedWord = porterStemmer.stemWord(word);

            result.add(Pair.of(stemmedWord, pos));
        }

        return result;
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

        return nGrams;
    }

    public static List<String> biGramWordPosPairs(List<Pair<String, String>> wordPosPairs) {
        List<String> biGrams = new LinkedList<>();

        StringBuilder stringBuilder = stringBuilderThreadLocal.get();
        for (int i = 0; i < wordPosPairs.size() - 1; i++) {
            String firstWord = wordPosPairs.get(i).getFirst();
            String firstPos = wordPosPairs.get(i).getSecond();
            String secondWord = wordPosPairs.get(i + 1).getFirst();
            String secondPos = wordPosPairs.get(i + 1).getSecond();

            for (List<String> grammaticalPattern : biGramGrammaticalPatterns) {
                String firstWordPos = grammaticalPattern.get(0);
                String secondWordPos = grammaticalPattern.get(1);

                if (firstPos.startsWith(firstWordPos) && secondPos.startsWith(secondWordPos)) {
                    stringBuilder.append(firstWord);
                    stringBuilder.append(" ");
                    stringBuilder.append(secondWord);

                    biGrams.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    break;
                }
            }
        }

        return biGrams;
    }

    public static List<String> triGramWordPosPairs(List<Pair<String, String>> wordPosPairs) {
        List<String> triGrams = new LinkedList<>();

        StringBuilder stringBuilder = stringBuilderThreadLocal.get();
        for (int i = 0; i < wordPosPairs.size() - 2; i++) {
            String firstWord = wordPosPairs.get(i).getFirst();
            String firstPos = wordPosPairs.get(i).getSecond();
            String secondWord = wordPosPairs.get(i + 1).getFirst();
            String secondPos = wordPosPairs.get(i + 1).getSecond();
            String thirdWord = wordPosPairs.get(i + 2).getFirst();
            String thirdPos = wordPosPairs.get(i + 2).getSecond();

            for (List<String> grammaticalPattern : triGramGrammaticalPatterns) {
                String firstWordPos = grammaticalPattern.get(0);
                String secondWordPos = grammaticalPattern.get(1);
                String thirdWordPos = grammaticalPattern.get(2);

                if (firstPos.startsWith(firstWordPos) && secondPos.startsWith(secondWordPos) && thirdPos.startsWith(thirdWordPos)) {
                    stringBuilder.append(firstWord);
                    stringBuilder.append(" ");
                    stringBuilder.append(secondWord);
                    stringBuilder.append(" ");
                    stringBuilder.append(thirdWord);

                    triGrams.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    break;
                }
            }
        }

        return triGrams;
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
