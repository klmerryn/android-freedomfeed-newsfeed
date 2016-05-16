package com.example.kl.freedomfeed;

import org.junit.Test;
import com.example.kl.freedomfeed.RssAtomFeedRetriever;

import static org.junit.Assert.*;

    import java.io.IOException;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.List;
    import java.util.ArrayList;
    import java.io.InputStreamReader;
    import java.util.concurrent.Exchanger;


    import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
    import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
    import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeedImpl;
    import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
    import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
    import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedOutput;
    import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;



/**
 * Created by kl on 09/05/16.
 */
public class FeedReaderTest {
    final String URL1 = "";
    final String URL2 = "";
    final String BADURL = "";


    @Test
    public void testSingleUrl(){
        String[] urls = {URL1};

    }

    @Test
    public void testMultiUrl(){

    }

}
