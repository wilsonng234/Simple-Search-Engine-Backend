package com.github.wilsonng234.Simple.Search.Engine.Backend.service;

import com.github.wilsonng234.Simple.Search.Engine.Backend.util.NLPUtils;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CrawlerService {
    private static final Logger logger = LogManager.getLogger(CrawlerService.class);
    public static SimpleDateFormat simpleDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

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

                logger.debug(this.document.equals(org.jsoup.Jsoup.connect(this.url).get()));
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

        private List<String> getChildrenLinks() {
            List<String> childrenLinks = new LinkedList<>();

            Elements linksElements = document.select("a[href]");
            for (Element linkElement : linksElements) {
                String childLink = linkElement.attr("abs:href");
                try {
                    new URL(childLink).toURI();
                } catch (MalformedURLException | URISyntaxException e) {
                    // Ignore invalid links
                    logger.warn(e.getMessage());
                }

                childrenLinks.add(childLink);
            }

            return childrenLinks;
        }
    }

    public boolean crawl(String url, String pages) {
        // Return: a boolean value indicating whether the crawling was successful or not
        // TODO: Implement this method
        int pagesToCrawl = Integer.parseInt(pages);
        int crawledPages = 0;

        Queue<Crawler> crawlers = new LinkedList<>();
        crawlers.add(new Crawler(url));
        Set<String> crawledLinks = new HashSet<>();
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
                logger.warn(exception.getMessage());
                continue;
            }
            // skip already indexed and no further update is needed
            
            logger.error(crawler.getSize());
            logger.error(crawler.getTitle());
            try {
                logger.error(crawler.getLastModificationDate());
            } catch (ParseException e) {
                logger.warn(e.getMessage());
                continue;
            }
            logger.error(crawler.getTitleWords());
            logger.error(crawler.getBodyWords());
            logger.error(crawler.getChildrenLinks());


            crawledLinks.add(crawler.getUrl());
            crawledPages++;
        }

        return true;
    }
}