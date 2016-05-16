package com.example.kl.freedomfeed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;

/*
 * Display integrated list of posts found by aggregating user's feed choices.
 * Uses another thread to parse and deliver feeds by instantiating an RssAtomFeedRetriever.
 *
 * ArrayList<String> inputFeeds         user-selected URLs
 *
 */
public class DisplayFeed extends AppCompatActivity {

    private ArrayList<String> inputFeeds;
    private static final String TITLE = "Recent News";

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE);
        setContentView(R.layout.activity_display_feed);
        Intent intent = getIntent();

        // Retrieve list of URLs early to pass to FeedListAdapter
        ArrayList<String> feedList = intent.getStringArrayListExtra("feedList");
        this.inputFeeds = feedList;
        Log.d("display feeds", feedList.toString());

        // Separate thread for feed fetching/parsing.
        AsyncTask task = new FeedProgressTask(this, this.inputFeeds).execute();
    }

    // @TODO: refactor properly so that onResume and onCreate share a helper method.
    // This was causing problems and requires more attention.
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        setContentView(R.layout.activity_display_feed);
        ArrayList<String> feedList = intent.getStringArrayListExtra("feedList");
        this.inputFeeds = feedList;
        Log.d("display feeds", feedList.toString());

        // Separate thread for feed fetching/parsing.
        AsyncTask task = new FeedProgressTask(this, this.inputFeeds).execute();
    }

    // Return to adding feeds
    public void goBack(View view) {
        Intent intent = new Intent(this, AddFeeds.class);
        intent.putStringArrayListExtra("inputFeeds", this.inputFeeds);
        startActivity(intent);
    }

    /*
     * Asynchronous task to handle fetching URLs and retrieving individual posts from
     * feed to display in the ListView.
     * Displays loading message, then handles 'work' of creating RssAtomFeedRetriever object
     * and fetching feeds.
     * @param   Activity activity           current activity
     * @param   ArrayList<String> inFeeds   user-chosed URLs representing feeds
     *
     */
    private class FeedProgressTask extends AsyncTask<String, Void, Boolean> {
        private Activity activity;
        private ArrayList<String> inFeeds;
        private RssAtomFeedRetriever feedRetriever;
        private ProgressDialog dialog;

        public FeedProgressTask(Activity activity, ArrayList<String> inFeeds) {
            this.activity = activity;
            this.inFeeds = inFeeds;
        }

        @Override
        protected void onPreExecute(){

            // Loading message so users know something's happening!
            dialog = new ProgressDialog(activity);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("Background operation","");
            try {
                this.feedRetriever = new RssAtomFeedRetriever();
                feedRetriever.aggregateRecentNews(inFeeds);
                return true;

            } catch (Exception e) {
                Log.e("error", e.getStackTrace().toString());
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result) { // Successful feed retrieval

                RelativeLayout layout = (RelativeLayout) findViewById(R.id.feed_display_layout_relative);
                ListView listView = (ListView) findViewById(R.id.rsslist);

                final CustomFeedAdapter feedListAdapter = new CustomFeedAdapter(this.activity, feedRetriever.feedEntries);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                        feedListAdapter.click(position);
                    }
                });
                listView.setAdapter(feedListAdapter);

                if (listView.getParent() != null) {
                    Log.d("remove listView parent","");
                    ((RelativeLayout)listView.getParent()).removeView(listView);
                }
                layout.addView(listView);
            }
            else { // Background work failed
                Toast.makeText(this.activity, "Sorry! Loading failed--please restart", Toast.LENGTH_SHORT).show();
                Log.d("doBgnd false", feedRetriever.feedEntries.toString());
            }
        }
    }

    /*
     * Custom adapter for ListView to display feed results.
     *
     * @param   List<SyndEntry> listEntries     list of feed entry objects
     * @param   Activity activity               current activity
     *
     */
    private class CustomFeedAdapter extends BaseAdapter {

        private List<SyndEntry> listEntries;
        private Activity activity;

        public CustomFeedAdapter(Activity context, List<SyndEntry> entries) {
            this.listEntries = entries;
            this.activity = context;
        }

        @Override
        public int getCount() {
            return this.listEntries.size();
        }

        @Override
        public SyndEntry getItem(int position) {
            return (SyndEntry)this.listEntries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // Build entries with custom row layout, then inflate and display
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView postTitleView;

            if (convertView == null) {
                postTitleView = (TextView) getLayoutInflater().inflate(R.layout.row_item, parent, false);
            }
            else {
                postTitleView = (TextView) convertView;
            }

            final SyndEntry post = getItem(position);
            postTitleView.setText(post.getTitle());

            return postTitleView;
        }

        public void click(int position) {
            String url = getItem(position).getLink();
            Log.d("retrieved url", url);

            // Format for web intent
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Log.d("parsed", Uri.parse(url).toString());

            this.activity.startActivity(webIntent);
        }
    }
}
