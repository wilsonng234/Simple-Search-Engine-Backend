package com.github.wilsonng234.simplesearchengine.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NLPUtilsTest {

    @Test
    void partsOfSpeech() {
        String text = "This is a test sentence.\n" +
                "This is another test sentence. " +
                "They are sentences.";
        List<Pair<String, String>> partsOfSpeech = NLPUtils.partsOfSpeech(text);

        assertEquals(
                List.of(
                        Pair.of("This", "DT"),
                        Pair.of("is", "VBZ"),
                        Pair.of("a", "DT"),
                        Pair.of("test", "NN"),
                        Pair.of("sentence", "NN"),
                        Pair.of(".", "."),
                        Pair.of("This", "DT"),
                        Pair.of("is", "VBZ"),
                        Pair.of("another", "DT"),
                        Pair.of("test", "NN"),
                        Pair.of("sentence", "NN"),
                        Pair.of(".", "."),
                        Pair.of("They", "PRP"),
                        Pair.of("are", "VBP"),
                        Pair.of("sentences", "NNS"),
                        Pair.of(".", ".")
                ),
                partsOfSpeech
        );
    }

    @Test
    void tokenize() {
        String text = "a set of words that is complete in itself,\n" +
                "typically containing a subject and predicate, " +
                "conveying a statement, question, exclamation\n" +
                "or command, and consisting of a main clause and sometimes one or more subordinate clauses.";
        List<String> tokens = NLPUtils.tokenize(text);

        assertEquals(
                List.of(
                        "a", "set", "of", "words", "that", "is", "complete", "in", "itself,",
                        "typically", "containing", "a", "subject", "and", "predicate,",
                        "conveying", "a", "statement,", "question,", "exclamation",
                        "or", "command,", "and", "consisting", "of", "a", "main", "clause",
                        "and", "sometimes", "one", "or", "more", "subordinate", "clauses."
                ),
                tokens
        );
    }

    @Test
    void removeStopWords() {
    }

    @Test
    void removeStopWordPosPairs() {
    }

    @Test
    void removePunctuations() {
    }

    @Test
    void removePunctuationsWordPosPairs() {
    }

    @Test
    void stemWord() {
    }

    @Test
    void stemWords() {
        List<String> words = List.of(
                "This", "is", "a", "test", "sentence",
                "This", "is", "another", "test", "sentence",
                "They", "are", "sentences"
        );
        List<String> stemmedWords = NLPUtils.stemWords(words);

        assertEquals(
                List.of(
                        "thi", "is", "a", "test", "sentenc",
                        "thi", "is", "anoth", "test", "sentenc",
                        "thei", "ar", "sentenc"
                ),
                stemmedWords
        );
    }

    @Test
    void stemWordPosPairs() {
    }

    @Test
    void nGrams() {
    }

    @Test
    void biGramWordPosPairs() {
    }

    @Test
    void triGramWordPosPairs() {
    }

    @Test
    void parsePhraseSearchQuery() {
    }
}