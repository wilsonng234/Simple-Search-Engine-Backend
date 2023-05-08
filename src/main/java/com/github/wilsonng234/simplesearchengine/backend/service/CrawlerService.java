package com.github.wilsonng234.simplesearchengine.backend.service;

import com.github.wilsonng234.simplesearchengine.backend.model.Document;
import com.github.wilsonng234.simplesearchengine.backend.model.PageRank;
import com.github.wilsonng234.simplesearchengine.backend.model.ParentLink;
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
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Autowired
    private WordService wordService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PostingService postingService;
    @Autowired
    private ParentLinkService parentLinkService;
    @Autowired
    private TermWeightService termWeightService;
    @Autowired
    private PageRankService pageRankService;

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
            if (lastModificationDate != null) {
                try {
                    return simpleDateFormat.parse(lastModificationDate).getTime();
                } catch (NumberFormatException numberFormatException) {
                    logger.error("lastModificationDate " + lastModificationDate + " " + url);
                }
            }

            String date = response.header("Date");
            if (date != null) {
                try {
                    return simpleDateFormat.parse(date).getTime();
                } catch (NumberFormatException numberFormatException) {
                    logger.error("date " + date + " " + url);
                }
            }

            return new Date().getTime();
        }

        private List<String> getTitleWords() {
            List<Pair<String, String>> wordPosPairs = NLPUtils.partsOfSpeech(document.head().text());
            wordPosPairs = NLPUtils.removePunctuationsWordPosPairs(wordPosPairs, true);
            wordPosPairs = NLPUtils.stemWordPosPairs(wordPosPairs);

            List<String> words = new ArrayList<>(
                    NLPUtils.removeStopWordPosPairs(wordPosPairs)
                            .stream().map(Pair::getFirst).toList()
            );
            words.addAll(NLPUtils.biGramWordPosPairs(wordPosPairs));
            words.addAll(NLPUtils.triGramWordPosPairs(wordPosPairs));

            return words;
        }

        private List<String> getBodyWords() {
            List<Pair<String, String>> wordPosPairs = NLPUtils.partsOfSpeech(document.body().text());
            wordPosPairs = NLPUtils.removePunctuationsWordPosPairs(wordPosPairs, true);
            wordPosPairs = NLPUtils.stemWordPosPairs(wordPosPairs);

            List<String> words = new ArrayList<>(
                    NLPUtils.removeStopWordPosPairs(wordPosPairs)
                            .stream().map(Pair::getFirst).toList()
            );
            words.addAll(NLPUtils.biGramWordPosPairs(wordPosPairs));
            words.addAll(NLPUtils.triGramWordPosPairs(wordPosPairs));

            return words;
        }

        private List<Pair<String, Integer>> getTitleWordFreqs() {
            List<String> words = getTitleWords();

            Map<String, Integer> wordFreqsMap = new HashMap<>();
            for (String word : words) {
                if (!wordFreqsMap.containsKey(word))
                    wordFreqsMap.put(word, 1);
                else
                    wordFreqsMap.put(word, wordFreqsMap.get(word) + 1);
            }

            List<Pair<String, Integer>> wordFreqsPairs = new ArrayList<>(wordFreqsMap.size());
            for (Map.Entry<String, Integer> entry : wordFreqsMap.entrySet()) {
                String word = entry.getKey();
                Integer freq = entry.getValue();

                wordFreqsPairs.add(Pair.of(word, freq));
            }

            wordFreqsPairs = CrawlerUtils.sortWordFreqs(wordFreqsPairs);
            return wordFreqsPairs;
        }

        private List<Pair<String, Integer>> getBodyWordFreqs() {
            List<String> words = getBodyWords();

            Map<String, Integer> wordFreqsMap = new HashMap<>();
            for (String word : words) {
                if (!wordFreqsMap.containsKey(word))
                    wordFreqsMap.put(word, 1);
                else
                    wordFreqsMap.put(word, wordFreqsMap.get(word) + 1);
            }

            List<Pair<String, Integer>> wordFreqsPairs = new ArrayList<>(wordFreqsMap.size());
            for (Map.Entry<String, Integer> entry : wordFreqsMap.entrySet()) {
                String word = entry.getKey();
                Integer freq = entry.getValue();

                wordFreqsPairs.add(Pair.of(word, freq));
            }

            wordFreqsPairs = CrawlerUtils.sortWordFreqs(wordFreqsPairs);
            return wordFreqsPairs;
        }

        private Set<String> getChildrenLinks() {
            Set<String> childrenLinks = new HashSet<>();

            Elements linksElements = document.select("a[href]");
            for (Element linkElement : linksElements) {
                String childLink = linkElement.attr("abs:href");
                if (!childLink.startsWith("http") || childLink.endsWith(".pdf"))
                    continue;
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
    }

    private void bfs(Queue<Crawler> crawlers, Set<String> crawledLinks, Set<String> childrenLinks, String url) {
        // breadth-first search
        for (String childLink : childrenLinks) {
            if (!crawledLinks.contains(childLink)) {
                crawlers.add(new Crawler(childLink));
            }

            ParentLink parentLink = new ParentLink(childLink, new HashSet<>(Collections.singleton(url)));
            parentLinkService.putParentLinks(parentLink);
        }
    }

    public boolean crawl(String url, String pages) {
        // Return: a boolean value indicating whether the crawling was successful or not
        long start = System.currentTimeMillis();
        int pagesToCrawl = Integer.parseInt(pages);
        int crawledPages = 0;

        Queue<Crawler> crawlers = new LinkedList<>();
        crawlers.add(new Crawler(url));
        Set<String> crawledLinks = new HashSet<>();

        ParentLink originParentLink = new ParentLink(url, new HashSet<>());
        parentLinkService.putParentLinks(originParentLink);

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

            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                if (document.getLastModificationDate() == lastModificationDate) {
                    // breadth-first search
                    bfs(crawlers, crawledLinks, crawler.getChildrenLinks(), crawler.getUrl());

                    continue;
                }
            }

            // get the document
            int titleMaxTF = 0;
            int bodyMaxTF = 0;
            long size = crawler.getSize();
            String title = crawler.getTitle();

            // set up titleWordFreqs
            List<Pair<String, Integer>> titleWordFreqs = crawler.getTitleWordFreqs();
            for (Pair<String, Integer> wordFreq : titleWordFreqs) {
                String word = wordFreq.getFirst();
                Integer freq = wordFreq.getSecond();
                wordService.getWord(word, WordService.QueryType.WORD)
                        .orElseGet(() -> wordService.putWord(word));
                titleMaxTF = Math.max(titleMaxTF, freq);
            }

            // set up bodyWordFreqs
            List<Pair<String, Integer>> bodyWordFreqs = crawler.getBodyWordFreqs();
            for (Pair<String, Integer> wordFreq : bodyWordFreqs) {
                String word = wordFreq.getFirst();
                Integer freq = wordFreq.getSecond();
                wordService.getWord(word, WordService.QueryType.WORD)
                        .orElseGet(() -> wordService.putWord(word));
                bodyMaxTF = Math.max(bodyMaxTF, freq);
            }

            Set<String> childrenLinks = crawler.getChildrenLinks();
            Document document = new Document(crawler.getUrl(), size, title, lastModificationDate,
                    titleWordFreqs, bodyWordFreqs, childrenLinks,
                    titleMaxTF, bodyMaxTF);

            // update the forward index
            document = documentService.putDocument(document);

            if (document == null) {
                // fail documentRepository.save(document);
                logger.warn("Fail to save document: " + crawler.getUrl());
                continue;
            }

            String docId = document.getDocId();
            for (Pair<String, Integer> wordFreq : titleWordFreqs) {
                String word = wordFreq.getFirst();
                Integer tf = wordFreq.getSecond();
                String wordId = wordService.getWord(word, WordService.QueryType.WORD).orElseGet(() -> {
                    // this is for double assurance
                    logger.warn("Word not found: " + word);
                    return wordService.putWord(word);
                }).getWordId();

                String type = "title";
                postingService.putPosting(wordId, type, docId, tf);
            }

            for (Pair<String, Integer> wordFreq : bodyWordFreqs) {
                String word = wordFreq.getFirst();
                Integer tf = wordFreq.getSecond();
                String wordId = wordService.getWord(word, WordService.QueryType.WORD).orElseGet(() -> {
                    // this is for double assurance
                    logger.warn("Word not found: " + word);
                    return wordService.putWord(word);
                }).getWordId();

                String type = "body";
                postingService.putPosting(wordId, type, docId, tf);
            }

            // put page rank
            PageRank pageRank = new PageRank(docId);
            pageRankService.putPageRank(pageRank);

            // breadth-first search
            bfs(crawlers, crawledLinks, childrenLinks, crawler.getUrl());

            crawledLinks.add(crawler.getUrl());
            crawledPages++;
        }
        long end = System.currentTimeMillis();
        logger.info("Crawled " + crawledPages + " pages in " + (end - start) / 1000.0 + " seconds");

        start = System.currentTimeMillis();
        termWeightService.updateTermWeights();
        end = System.currentTimeMillis();
        logger.info("Updated term weights in " + (end - start) / 1000.0 + " seconds");

        start = System.currentTimeMillis();
        pageRankService.updatePageRank();
        end = System.currentTimeMillis();
        logger.info("Updated page ranks in " + (end - start) / 1000.0 + " seconds");

        return true;
    }
}
