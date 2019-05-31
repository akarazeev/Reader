package com.github.pashmentov96.reader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenSlidePagerActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;

    private PagerAdapter pagerAdapter;

    String textOfBook;
    static final String ARGUMENT_TEXT = "argument_text";
    TextView translationOfWord;

    int numPages;

    public static String clickedWord;

    public static Intent getIntent(Context context, String text) {
        Intent intent = new Intent(context, ScreenSlidePagerActivity.class);
        intent.putExtra(ARGUMENT_TEXT, text);
        return intent;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.traslationOfWord) {
            if (clickedWord.length() > 0) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Toast.makeText(v.getContext(), clickedWord + " added to your wordlist", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        return addWord(clickedWord);
                    }
                }.execute();
            }
        }
    }

    private Void addWord(String word) {
        SomePreferences somePreferences = new SomePreferences(this);
        String token = somePreferences.getToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://d6719ff8.ngrok.io/api/add/" + word);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Bearer " + token);

            Log.d("MyLogs", "Code " + connection.getResponseCode() + "; " + "Message " + connection.getResponseMessage());
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        Toolbar myToolBar = findViewById(R.id.my_toolbar_2);
        setSupportActionBar(myToolBar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        translationOfWord = findViewById(R.id.traslationOfWord);
        translationOfWord.setOnClickListener(this);
        ScreenSlidePageFragment.translationOfWord = translationOfWord;

        textOfBook = getIntent().getStringExtra(ARGUMENT_TEXT);
        numPages = (textOfBook.length() + 199) / 200;

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int beginIndex = 200 * position;
            return ScreenSlidePageFragment.newInstance(position, textOfBook.substring(beginIndex, beginIndex + 200));
        }

        @Override
        public int getCount() {
            return numPages;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

}
