package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.*;
import com.github.wilsonng234.simplesearchengine.backend.util.NLPUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.SearchEngineUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.VSMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchEngineService {
    @Autowired
    private WordService wordService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private TitlePostingListService titlePostingListService;
    @Autowired
    private BodyPostingListService bodyPostingListService;
    private List<Word> words;
    private List<Document> documents;
    private List<List<Double>> documentsVector;     // index1: docId, index2: wordID, value: termWeight
    private List<Double> queryVector;               // index: wordID, value: termWeight
    private List<Double> scoresVector;              // index: docId, value: score
    private Map<String, Integer> wordsMap;          // key: wordId, value: index
    private Map<String, Integer> documentsMap;      // key: docId, value: index

    public List<Document> search(String query) {
        setUp();
        setUpQueryVector(query);
        setUpScoresVector();

        System.out.println("Query vector: " + queryVector);
        System.out.println("Scores vector: " + scoresVector);

        List<Integer> topKIndices = SearchEngineUtils.getTopKIndices(scoresVector, 50);
        return topKIndices.stream()
                .map(index -> documents.get(index))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void setUpQueryVector(String query) {
        // TODO: Fix the query vector computation if needed
        List<String> queryWords = NLPUtils.tokenize(query);
        queryWords = NLPUtils.removeStopWords(queryWords);
        queryWords = NLPUtils.stemWords(queryWords);

        for (String queryWord : queryWords) {
            Optional<Word> wordIdOptional = wordService.getWord(queryWord, WordService.QueryType.WORD);
            if (wordIdOptional.isPresent()) {
                String wordId = wordIdOptional.get().getWordId();
                Integer wordIndex = wordsMap.get(wordId);

                if (wordIndex != null)
                    queryVector.set(wordIndex, queryVector.get(wordIndex) + 1.0);
            }
        }
    }

    private void setUpScoresVector() {
        for (int i = 0; i < documents.size(); i++) {
            List<Double> documentVector = documentsVector.get(i);
            double score = VSMUtils.getCosineSimilarity(documentVector, queryVector);

            scoresVector.set(i, score);
        }
    }

    private void setUp() {
        words = wordService.allWords();
        documents = documentService.allDocuments();

        int i = 0;
        documentsMap = new HashMap<>();
        scoresVector = new ArrayList<>(documents.size());
        for (Document document : documents) {
            documentsMap.put(document.getDocId(), i);
            scoresVector.add(0.0);
            i += 1;
        }

        i = 0;
        wordsMap = new HashMap<>();
        queryVector = new ArrayList<>(words.size());
        for (Word word : words) {
            wordsMap.put(word.getWordId(), i);
            queryVector.add(0.0);
            i += 1;
        }

        documentsVector = new ArrayList<>(documents.size());
        for (i = 0; i < documents.size(); i++) {
            documentsVector.add(new ArrayList<>(words.size()));
            for (int j = 0; j < words.size(); j++) {
                documentsVector.get(i).add(0.0);
            }
        }

        setUpDocumentsVector();
    }

    private void setUpDocumentsVector() {
        // TODO: fix the maxTF and df computation if needed
        int numDocs = documents.size();
        for (Word word : words) {
            String wordId = word.getWordId();
            int wordIndex = wordsMap.get(wordId);

            TitlePostingList titlePostingList = titlePostingListService.getPostingList(wordId);
            BodyPostingList bodyPostingList = bodyPostingListService.getPostingList(wordId);
            int titleMaxTF = titlePostingList.getMaxTF();
            int titleDocFreq = titlePostingList.getPostings().size();
            int bodyMaxTF = bodyPostingList.getMaxTF();
            int bodyDocFreq = bodyPostingList.getPostings().size();

            for (Posting posting : titlePostingList.getPostings()) {
                String docId = posting.getDocId();
                Integer docIndex = documentsMap.get(docId);
                if (docIndex == null)
                    continue;
                List<Long> positions = posting.getWordPositions();
                int tf = positions.size();

                double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                double additionTermWeight = VSMUtils.getTermWeight(tf, numDocs, titleDocFreq, titleMaxTF);
                documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
            }

            for (Posting posting : bodyPostingList.getPostings()) {
                String docId = posting.getDocId();
                Integer docIndex = documentsMap.get(docId);
                if (docIndex == null)
                    continue;
                List<Long> positions = posting.getWordPositions();
                int tf = positions.size();

                double originTermWeight = documentsVector.get(docIndex).get(wordIndex);
                double additionTermWeight = VSMUtils.getTermWeight(tf, numDocs, bodyDocFreq, bodyMaxTF);
                documentsVector.get(docIndex).set(wordIndex, originTermWeight + additionTermWeight);
            }
        }
    }
}
