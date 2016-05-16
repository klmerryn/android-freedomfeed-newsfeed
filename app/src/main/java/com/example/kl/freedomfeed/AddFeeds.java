package com.example.kl.freedomfeed;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class AddFeeds extends AppCompatActivity {

    // Temporary solution prior to actual database implementation.
    // stock URLs on different themes.
    final static String[] HUMAN_RIGHTS = new String[]{"http://www.democracynow.org/democracynow.rss"};
    final static String[] PRIVACY = new String[]{"https://cyberlaw.stanford.edu/feeds/focus-area/privacy.rss",
            "https://cyberlaw.stanford.edu/feeds/focus-area/architecture-and-public-policy.rss"};
    final static String[] CENSORSHIP = new String[]{"https://queryfeed.net/tw?q=internetcensorship"};
    final static String[] CITIZEN_EDU = new String[]{"http://esj.sagepub.com/rss/recent.xml", "http://esj.sagepub.com/rss/ahead.xml", "http://esj.sagepub.com/rss/mfr.xml"};

    // Button display
    private final String SUBSCRIBE = "Subscribe";
    private final String CANCEL = "Cancel";

    // Build list of feeds. Temporary solution pending persistent data.
    ArrayList<String> feeds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeds);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = getIntent();
//        feeds = intent.getStringArrayListExtra("inputFeeds");
//        setContentView(R.layout.activity_add_feeds);
//    }

    private void setCustomFeed() {

        // Get layout view (XML) for prompt popup
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

        // Set up dialog window.
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(SUBSCRIBE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //@TODO: Input will be parsed and verified as a single well-formatted string.
                        // This will involve another AsyncTask to verify the URL. Invalid URL
                        // should erase the edittextview and prompt the user to enter new URL or
                        /// to cancel.
                        String[] custom = new String[]{editText.getText().toString()};
                        Log.d("Custom feed:", custom.toString());
                        addFeed(feeds, custom);
                    }
                })
                .setNegativeButton(CANCEL,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // Create dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void addFeed(ArrayList<String> current, String[] newFeed) {
        for (String item : newFeed) {
            current.add(item);
        }
    }

    // Pending a refactor with database of topics and URLs, this is temporary solution.
    public void addToFeed(View view){
        switch (view.getId()) {
            case (R.id.human_rights):
                addFeed(this.feeds, HUMAN_RIGHTS);
                break;
            case (R.id.censorship):
                addFeed(this.feeds, CENSORSHIP);
                break;
            case (R.id.privacy):
                addFeed(this.feeds, PRIVACY);
                break;
            case (R.id.citizen_edu):
                addFeed(this.feeds, CITIZEN_EDU);
                break;
            case (R.id.custom):
                setCustomFeed();
                break;
        }
    }


    public void goMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goDisplayFeed(View view){
        Intent intent = new Intent(this, DisplayFeed.class);
        intent.putStringArrayListExtra("feedList", feeds);
        startActivity(intent);
    }
}
