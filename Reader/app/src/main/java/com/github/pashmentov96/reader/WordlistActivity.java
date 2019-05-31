package com.github.pashmentov96.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class WordlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        Toolbar childToolbar = findViewById(R.id.my_toolbar_2);
        setSupportActionBar(childToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final TextView myDictionary = findViewById(R.id.myDictionary);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return loadWordlist();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("MyLogs", "Wordlist + " + s);
                if (s != null && (s.length() < 5 || !s.substring(0, 5).equals("Error"))) {
                    Map<String, String> dictionary = parseWordlistFromJson(s);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String key : dictionary.keySet()) {
                        stringBuilder.append(key + " - " + dictionary.get(key) + "\n");
                    }
                    if (stringBuilder.length() != 0) {
                        myDictionary.setText(stringBuilder.toString());
                    }
                }
            }
        }.execute();
    }

    private Map<String, String> parseWordlistFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            HashMap<String, String> wordlist = new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                Log.d("Wordlist", key + ": " + value);
                wordlist.put(key, value);
            }
            return wordlist;
        } catch (JSONException e) {
            return null;
        }
    }

    private String loadWordlist() {
        SomePreferences somePreferences = new SomePreferences(this);
        String token = somePreferences.getToken();
        Log.d("MyLogs", "TOKEN = " + token);
        try {

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet("http://d6719ff8.ngrok.io/api/wordlist");
            httpGet.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = httpclient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            Log.d("MyLogs", "Code " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error connection";
            } else {
                String stringResponse = EntityUtils.toString(response.getEntity());
                return stringResponse;
            }
        } catch (IOException ex) {
            return "Error " + ex.getMessage();
        }
    }
}
