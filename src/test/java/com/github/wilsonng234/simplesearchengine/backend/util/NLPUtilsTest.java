package com.github.wilsonng234.simplesearchengine.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        List<String> words = List.of(
                "thi", "is", "a", "test", "sentenc",
                "thi", "is", "anoth", "test", "sentenc",
                "thei", "ar", "sentenc"
        );

        List<String> result = NLPUtils.removeStopWords(words);
        assertEquals(
                List.of(
                        "test", "sentenc",
                        "test", "sentenc",
                        "sentenc"
                ),
                result
        );
    }

    @Test
    void removeStopWordPosPairs() {
        List<Pair<String, String>> wordPosPairs = List.of(
                Pair.of("thi", "DT"), Pair.of("is", "VBZ"), Pair.of("a", "DT"), Pair.of("test", "NN"), Pair.of("sentenc", "NN"),
                Pair.of("thi", "DT"), Pair.of("is", "VBZ"), Pair.of("anoth", "DT"), Pair.of("test", "NN"), Pair.of("sentenc", "NN"),
                Pair.of("thei", "DT"), Pair.of("ar", "VBZ"), Pair.of("sentenc", "NN")
        );

        List<Pair<String, String>> result = NLPUtils.removeStopWordPosPairs(wordPosPairs);
        assertEquals(
                List.of(
                        Pair.of("test", "NN"), Pair.of("sentenc", "NN"),
                        Pair.of("test", "NN"), Pair.of("sentenc", "NN"),
                        Pair.of("sentenc", "NN")
                ),
                result
        );
    }

    @Test
    void removePunctuations() {
        List<String> words = List.of(
                "$!2324,$#@", "213$231", ":helloworld,", "!hello:world:2022,1,2,", "\"hello:world:2022,1,2",
                "hello:world:2022,1,2\"", "\"hello:world:2022,1,2\"", "hello:world:2022,1,2"
        );

        boolean removeDoubleQuotationMarks = true;
        List<String> removePunctuations = NLPUtils.removePunctuations(words, removeDoubleQuotationMarks);
        assertEquals(
                List.of(
                        "2324", "213$231", "helloworld", "hello:world:2022,1,2", "hello:world:2022,1,2",
                        "hello:world:2022,1,2", "hello:world:2022,1,2", "hello:world:2022,1,2"
                ),
                removePunctuations
        );

        removeDoubleQuotationMarks = false;
        removePunctuations = NLPUtils.removePunctuations(words, removeDoubleQuotationMarks);
        assertEquals(
                List.of(
                        "2324", "213$231", "helloworld", "hello:world:2022,1,2", "\"hello:world:2022,1,2",
                        "hello:world:2022,1,2\"", "\"hello:world:2022,1,2\"", "hello:world:2022,1,2"
                ),
                removePunctuations
        );
    }

    @Test
    void removePunctuationsWordPosPairs() {
        List<Pair<String, String>> wordPosPairs = List.of(
                Pair.of("$!2324,$#@", "DT"),
                Pair.of("213$231", "VBZ"),
                Pair.of(":helloworld,", "DT"),
                Pair.of("!hello:world:2022,1,2,", "NN"),
                Pair.of("\"hello:world:2022,1,2", "NN"),
                Pair.of("hello:world:2022,1,2\"", "DT"),
                Pair.of("\"hello:world:2022,1,2\"", "VBZ"),
                Pair.of("hello:world:2022,1,2", "DT")
        );

        boolean removeDoubleQuotationMarks = true;
        List<Pair<String, String>> removePunctuations = NLPUtils.removePunctuationsWordPosPairs(wordPosPairs, removeDoubleQuotationMarks);
        assertEquals(
                List.of(
                        Pair.of("2324", "DT"),
                        Pair.of("213$231", "VBZ"),
                        Pair.of("helloworld", "DT"),
                        Pair.of("hello:world:2022,1,2", "NN"),
                        Pair.of("hello:world:2022,1,2", "NN"),
                        Pair.of("hello:world:2022,1,2", "DT"),
                        Pair.of("hello:world:2022,1,2", "VBZ"),
                        Pair.of("hello:world:2022,1,2", "DT")
                ),
                removePunctuations
        );

        removeDoubleQuotationMarks = false;
        removePunctuations = NLPUtils.removePunctuationsWordPosPairs(wordPosPairs, removeDoubleQuotationMarks);
        assertEquals(
                List.of(
                        Pair.of("2324", "DT"),
                        Pair.of("213$231", "VBZ"),
                        Pair.of("helloworld", "DT"),
                        Pair.of("hello:world:2022,1,2", "NN"),
                        Pair.of("\"hello:world:2022,1,2", "NN"),
                        Pair.of("hello:world:2022,1,2\"", "DT"),
                        Pair.of("\"hello:world:2022,1,2\"", "VBZ"),
                        Pair.of("hello:world:2022,1,2", "DT")
                ),
                removePunctuations
        );
    }

    @Test
    void stemWord() {
        String word = "THIS";
        String stemmedWord = NLPUtils.stemWord(word);
        assertEquals("thi", stemmedWord);
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
        List<Pair<String, String>> wordPosPairs = List.of(
                Pair.of("This", "DT"),
                Pair.of("is", "VBZ"),
                Pair.of("a", "DT"),
                Pair.of("test", "NN"),
                Pair.of("sentence", "NN"),
                Pair.of("This", "DT"),
                Pair.of("is", "VBZ"),
                Pair.of("another", "DT"),
                Pair.of("test", "NN"),
                Pair.of("sentence", "NN"),
                Pair.of("They", "PRP"),
                Pair.of("are", "VBP"),
                Pair.of("sentences", "NNS")
        );
        List<Pair<String, String>> stemmedWordPosPairs = NLPUtils.stemWordPosPairs(wordPosPairs);

        assertEquals(
                List.of(
                        Pair.of("thi", "DT"),
                        Pair.of("is", "VBZ"),
                        Pair.of("a", "DT"),
                        Pair.of("test", "NN"),
                        Pair.of("sentenc", "NN"),
                        Pair.of("thi", "DT"),
                        Pair.of("is", "VBZ"),
                        Pair.of("anoth", "DT"),
                        Pair.of("test", "NN"),
                        Pair.of("sentenc", "NN"),
                        Pair.of("thei", "PRP"),
                        Pair.of("ar", "VBP"),
                        Pair.of("sentenc", "NNS")
                ),
                stemmedWordPosPairs
        );
    }

    @Test
    void nGrams() {
        String sentence = "Applicants for admission to the postgraduate programs are required to have  completed ";
        List<String> words = List.of(sentence.split("\\s+"));
        List<String> biGrams = NLPUtils.nGrams(words, 2);
        List<String> triGrams = NLPUtils.nGrams(words, 3);

        assertEquals(
                List.of(
                        "Applicants for", "for admission", "admission to", "to the", "the postgraduate",
                        "postgraduate programs", "programs are", "are required", "required to", "to have",
                        "have completed"
                ),
                biGrams
        );

        assertEquals(
                List.of(
                        "Applicants for admission", "for admission to", "admission to the", "to the postgraduate",
                        "the postgraduate programs", "postgraduate programs are", "programs are required",
                        "are required to", "required to have", "to have completed"
                ),
                triGrams
        );

        assertEquals(
                new ArrayList<>(),
                NLPUtils.nGrams(new ArrayList<>(), 2)
        );
    }

    @Test
    void biGramWordPosPairs() {
    }

    @Test
    void triGramWordPosPairs() {
    }

    @Test
    void parsePhraseSearchQuery() {
        String query = "hello world \"test page\" movie \"movie\" \"movie test page\" testing";

        List<String> phrases = NLPUtils.parsePhraseSearchQuery(query);
        assertEquals(
                List.of(
                        "test page", "movie", "movie test page"
                ),
                phrases
        );

        try {
            // invalid query should not break the program
            query = "hello world \"this\" query\" is \" invalid\"";
            NLPUtils.parsePhraseSearchQuery(query);
        } catch (Exception e) {
            fail();
        }
    }
}