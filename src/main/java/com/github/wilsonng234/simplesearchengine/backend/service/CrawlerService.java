package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
import com.github.wilsonng234.simplesearchengine.backend.model.Posting;
import com.github.wilsonng234.simplesearchengine.backend.util.CrawlerUtils;
import com.github.wilsonng234.simplesearchengine.backend.util.NLPUtils;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Scope("prototype")
public class CrawlerService {
    private static final Logger logger = LogManager.getLogger(CrawlerService.class);
    public static SimpleDateFormat simpleDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Autowired
    private WordService wordService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private TitlePostingListService titlePostingListService;
    @Autowired
    private BodyPostingListService bodyPostingListService;
    @Autowired
    private ParentLinkService parentLinkService;

    @Data
    private class Crawler {
        private final String url;
        private org.jsoup.nodes.Document document;
        private org.jsoup.Connection.Response response;

        public Crawler(String url) {
            this.url = url;
            this.document = null;
            this.response = null;
        }

        private void request() throws IOException {
            if (this.response == null) {
                this.response = org.jsoup.Jsoup.connect(this.url).execute().bufferUp();
                this.document = this.response.parse();
            }
        }

        private long getSize() {
            String contentLength = response.header("Content-Length");
            if (contentLength != null)
                return Long.parseLong(contentLength);

            return response.body().length();
        }

        private String getTitle() {
            return document.title();
        }

        private long getLastModificationDate() throws ParseException {
            String lastModificationDate = response.header("Last-Modified");
            if (lastModificationDate != null)
                return simpleDateFormat.parse(lastModificationDate).getTime();

            String date = response.header("Date");
            if (date != null)
                return simpleDateFormat.parse(date).getTime();

            return new Date().getTime();
        }

        private List<String> getTitleWords() {
            List<String> words = NLPUtils.tokenize(document.head().text());
            words = NLPUtils.removeStopWords(words);
            words = NLPUtils.stemWords(words);

            return words;
        }

        private List<String> getBodyWords() {
            List<String> words = NLPUtils.tokenize(document.body().text());
            words = NLPUtils.removeStopWords(words);
            words = NLPUtils.stemWords(words);

            return words;
        }

        private Set<String> getChildrenLinks() {
            Set<String> childrenLinks = new HashSet<>();

            Elements linksElements = document.select("a[href]");
            for (Element linkElement : linksElements) {
                String childLink = linkElement.attr("abs:href");
                try {
                    new URL(childLink).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    // Ignore invalid links
//                    logger.warn(e.getMessage());
                    continue;
                }

                childrenLinks.add(childLink);
            }

            return childrenLinks;
        }

        private Map<String, List<Long>> getTitleWordPositions() {
            List<String> words = NLPUtils.tokenize(document.head().text());
            words = NLPUtils.removeStopWords(words);
            words = NLPUtils.stemWords(words);

            Map<String, List<Long>> wordPositions = new HashMap<>();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if (!wordPositions.containsKey(word))
                    wordPositions.put(word, new LinkedList<>());
                wordPositions.get(word).add((long) i);
            }

            List<String> biGrams = NLPUtils.nGrams(words, 2);
            List<String> triGrams = NLPUtils.nGrams(words, 3);

            // add biGrams
            for (int i = 0; i < biGrams.size(); i++) {
                String biGram = biGrams.get(i);
                if (!wordPositions.containsKey(biGram))
                    wordPositions.put(biGram, new LinkedList<>());
                wordPositions.get(biGram).add((long) i);
            }

            // add triGrams
            for (int i = 0; i < triGrams.size(); i++) {
                String triGram = triGrams.get(i);
                if (!wordPositions.containsKey(triGram))
                    wordPositions.put(triGram, new LinkedList<>());
                wordPositions.get(triGram).add((long) i);
            }

            return wordPositions;
        }

        private Map<String, List<Long>> getBodyWordPositions() {
            List<String> words = NLPUtils.tokenize(document.body().text());
            words = NLPUtils.removeStopWords(words);
            words = NLPUtils.stemWords(words);

            Map<String, List<Long>> wordPositions = new HashMap<>();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if (!wordPositions.containsKey(word))
                    wordPositions.put(word, new LinkedList<>());
                wordPositions.get(word).add((long) i);
            }

            List<String> biGrams = NLPUtils.nGrams(words, 2);
            List<String> triGrams = NLPUtils.nGrams(words, 3);

            // add biGrams
            for (int i = 0; i < biGrams.size(); i++) {
                String biGram = biGrams.get(i);
                if (!wordPositions.containsKey(biGram))
                    wordPositions.put(biGram, new LinkedList<>());
                wordPositions.get(biGram).add((long) i);
            }

            // add triGrams
            for (int i = 0; i < triGrams.size(); i++) {
                String triGram = triGrams.get(i);
                if (!wordPositions.containsKey(triGram))
                    wordPositions.put(triGram, new LinkedList<>());
                wordPositions.get(triGram).add((long) i);
            }

//            for (String biGram : biGrams)
//                if (biGram.split("\\s+").length != 2) {
//                    logger.warn("BiGram length != 2: " + biGram);
//
//            for (String triGram : triGrams)
//                if (triGram.split("\\s+").length != 3) {
//                    logger.warn("TriGram length != 3: " + triGram);

            return wordPositions;
        }
    }

    public boolean crawl(String url, String pages) {
        // Return: a boolean value indicating whether the crawling was successful or not
        int pagesToCrawl = Integer.parseInt(pages);
        int crawledPages = 0;

        Queue<Crawler> crawlers = new LinkedList<>();
        crawlers.add(new Crawler(url));
        Set<String> crawledLinks = new HashSet<>();

        Optional<ParentLink> optionalParentLink = parentLinkService.getParentLinks(url);
        if (optionalParentLink.isEmpty()) {
            ParentLink originParentLink = new ParentLink(url, new HashSet<>());
            parentLinkService.putParentLinks(originParentLink);
        }
        while (crawledPages < pagesToCrawl && !crawlers.isEmpty()) {
            Crawler crawler = crawlers.poll();

            // skip if the link has been crawled
            if (crawledLinks.contains(crawler.getUrl()))
                continue;
            crawledLinks.add(crawler.getUrl());
            // skip if the connection is not successful
            try {
                crawler.request();
            } catch (IOException exception) {
                logger.warn(exception.getMessage() + "\n" + crawler.getUrl());
                continue;
            }
            // skip already indexed and no further update is needed
            long lastModificationDate;
            try {
                lastModificationDate = crawler.getLastModificationDate();
            } catch (ParseException exception) {
                logger.warn(exception.getMessage());
                continue;
            }
            Optional<Document> optionalDocument = documentService.getDocument(crawler.getUrl(), DocumentService.QueryType.URL);

            boolean indexedDocument = false;
            if (optionalDocument != null) {
                indexedDocument = optionalDocument.isPresent();
                if (indexedDocument) {
                    Document document = optionalDocument.get();
                    if (document.getLastModificationDate() == lastModificationDate)
                        continue;
                }
            }

            // get the document
            long size = crawler.getSize();
            String title = crawler.getTitle();

            // set up titleWordFreqs
            Map<String, List<Long>> titleWordPositions = crawler.getTitleWordPositions();
            List<Pair<String, Integer>> titleWordFreqs = new ArrayList<>(titleWordPositions.size());
            for (Map.Entry<String, List<Long>> entry : titleWordPositions.entrySet()) {
                String titleWord = entry.getKey();
                wordService.getWord(titleWord, WordService.QueryType.WORD)
                        .orElseGet(() -> wordService.createWord(titleWord));
                List<Long> positions = entry.getValue();
                int freq = positions.size();

                titleWordFreqs.add(Pair.of(titleWord, freq));
            }
            titleWordFreqs = CrawlerUtils.sortWordFreqs(titleWordFreqs);

            // set up bodyWordFreqs
            Map<String, List<Long>> bodyWordPositions = crawler.getBodyWordPositions();
            List<Pair<String, Integer>> bodyWordFreqs = new ArrayList<>(bodyWordPositions.size());
            for (Map.Entry<String, List<Long>> entry : bodyWordPositions.entrySet()) {
                String bodyWord = entry.getKey();
                wordService.getWord(bodyWord, WordService.QueryType.WORD)
                        .orElseGet(() -> wordService.createWord(bodyWord));
                List<Long> positions = entry.getValue();
                int freq = positions.size();

                bodyWordFreqs.add(Pair.of(bodyWord, freq));
            }
            bodyWordFreqs = CrawlerUtils.sortWordFreqs(bodyWordFreqs);

            Set<String> childrenLinks = crawler.getChildrenLinks();
            Document document = new Document(crawler.getUrl(), size, title, lastModificationDate, titleWordFreqs, bodyWordFreqs, childrenLinks);

            // update the forward index
            if (indexedDocument) {
                document = documentService.putDocument(document);
            } else {
                document = documentService.createDocument(document);
            }
            if (document == null) {
                // fail documentRepository.save(document);
                logger.warn("Fail to save document: " + crawler.getUrl());
                continue;
            }

            String docId = document.getDocId();
            for (Map.Entry<String, List<Long>> wordPositions : titleWordPositions.entrySet()) {
                String word = wordPositions.getKey();
                List<Long> positions = wordPositions.getValue();
                String wordId = wordService.getWord(word, WordService.QueryType.WORD).orElseGet(() -> {
                    // this is for double assurance
                    logger.warn("Word not found: " + word);
                    return wordService.createWord(word);
                }).getWordId();

                Posting posting = new Posting(docId, positions);
                titlePostingListService.putPositingList(wordId, posting);
            }

            for (Map.Entry<String, List<Long>> wordPositions : bodyWordPositions.entrySet()) {
                String word = wordPositions.getKey();
                List<Long> positions = wordPositions.getValue();
                String wordId = wordService.getWord(word, WordService.QueryType.WORD).orElseGet(() -> {
                    // this is for double assurance
                    logger.warn("Word not found: " + word);
                    return wordService.createWord(word);
                }).getWordId();

                Posting posting = new Posting(docId, positions);
                bodyPostingListService.putPositingList(wordId, posting);
            }

            // breadth-first search
            for (String childLink : childrenLinks) {
                if (!crawledLinks.contains(childLink)) {
                    crawlers.add(new Crawler(childLink));

                    ParentLink parentLink = new ParentLink(childLink, new HashSet<>(Collections.singleton(crawler.getUrl())));
                    parentLinkService.putParentLinks(parentLink);
                }
            }

            crawledLinks.add(crawler.getUrl());
            crawledPages++;
        }

        return true;
    }
}
