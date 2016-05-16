package com.example.kl.freedomfeed;

/**
 * Created by kl on 12/05/16.
 */

import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.atom.Entry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeedImpl;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Class based on Google-repackaged version of ROME feed-parsing package.
 * Creates an instance of SyndFeedImpl, a type of SyndFeed object, and converts
 * URLs specified by user into Feed objects which are aggregated. Individual posts (entries)
 * are stored together as SyndEntries, which differs from typical RSS/ATOM feed readers that
 * only manage one feed at a time.
 *
 * @param   ArrayList<String> feedUrls          list of URLs of syndicated feeds
 */
public class RssAtomFeedRetriever {

    SyndFeedImpl aggregateFeed = new SyndFeedImpl();
    ArrayList<SyndEntry> feedEntries = new ArrayList<SyndEntry>();
    private final static int REVERSE = -1; // Sort posts in reverse chronological order
    private final static String HEADLINER = "Headlines for";
    private HashMap<SyndEntry, URL> entryUrlHashMap;

    private SyndFeed retrieveFeed(final String feedUrl)
            throws IOException, FeedException, FetcherException {
        FeedFetcher feedFetcher = new HttpURLFeedFetcher();
        return feedFetcher.retrieveFeed(new URL(feedUrl));
    }

    public SyndFeed aggregateRecentNews(final ArrayList<String> feedUrls) {

        // Associate a collection with the feed
        aggregateFeed.setEntries(feedEntries);

        try {
            // Aggregate
            for (String newUrl: feedUrls) {
                SyndFeed addUrl = retrieveFeed(newUrl);
                feedEntries.addAll(addUrl.getEntries());
            }
            // Sort entries reverse chronologically (recent first)
            // (When aggregating, separate feeds don't get interspersed)
            Collections.sort(feedEntries, new Comparator<SyndEntry>() {
                public int compare(SyndEntry entryOne, SyndEntry entryTwo) {
                    if (entryOne.getPublishedDate() == null || entryTwo.getPublishedDate() == null) {
                        return 0;
                    }
                    else {
                        return entryOne.getPublishedDate().compareTo(entryTwo.getPublishedDate()) * REVERSE;
                    }
                }
            });

            formatPosts(feedEntries);
            return aggregateFeed;
        }
        catch (Exception exception) {
            Log.e("retrieval failure", feedEntries.toString());
            throw new RuntimeException(exception);
        }
    }

    /*
     * Format posts to display full tweets (Twitter) and remove erroneous headers.
     * This should be integrated into better parsing as an optional add-on to the ROME library.
     * Use of iterator is necessary to avoid concurrent list modifcation.
     */
    private void formatPosts(ArrayList<SyndEntry> posts) {
        for (Iterator<SyndEntry> iterator = posts.iterator(); iterator.hasNext();) {
            SyndEntry post = iterator.next();

            // Future: parse twitter post to display tweet
            // Current: display author and hashtag(s)/topic
            if (post.getTitle().toString().startsWith("@")) {
                //Log.d("twitter post", post.getDescription().toString());
                post.setTitle(post.getTitle().toString().split(" ")[0] +
                        " (Twitter) " + findHashTag(post));
            }

            // Need to remove headlines formatted as posts -- to be fixed
            // with better parsing
            else if (post.getTitle().contains(HEADLINER)) {
                iterator.remove();
            }
        }
    }

    // Temporary method to isolate hashtags to add meaningful titles to tweets
    public String findHashTag(SyndEntry post) {
        String hashtag = "";
        String regex = "hashtag/([a-zA-Z]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(post.getDescription().toString());
        while (matcher.find()) {
            hashtag.concat("<" + matcher.group(1) + "> ");
            Log.d("hashtag is now ", hashtag);
            System.out.println("hashtag " + hashtag);
        }
        return hashtag;
    }

}

