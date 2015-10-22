package br.com.bsz.androiddevelopersblog;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.atom.Entry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.impl.Atom10Parser;

import org.jdom.JDOMException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class RssAtomFeedRetriever {

    private static final String BASE_URL = "https://www.blogger.com/feeds/6755709643044947179/posts/default?start-index=1&max-results=%s&redirect=false";
    public static final int SELF_LINK_POSITION = 1;
    public static final int SELF_LINK_ALTERNATE_POSITION = 0;

    public static SyndFeed getMostRecentNews(ClassLoader classLoader, String amountOfArticles) {
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            return retrieveFeed(String.format(BASE_URL, amountOfArticles));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Entry getEntry(String url) {
        try {
            return retrieveEntry(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Entry retrieveEntry(String url) throws IOException, JDOMException, FeedException {
        InputStream is = new URL(url).openConnection().getInputStream();
        return Atom10Parser.parseEntry(new BufferedReader(new InputStreamReader(is)), null);
    }

    private static SyndFeed retrieveFeed(final String feedUrl) throws IOException, FeedException, FetcherException {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
        return feedFetcher.retrieveFeed(new URL(feedUrl));
    }
}
