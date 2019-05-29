package com.github.pashmentov96.reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.Main;

public class MainActivity extends AppCompatActivity {

    Button buttonHistory;
    Button buttonOpen;

    ClickableTextView textView;

    final int PICK_FILE_REQUEST = 10;
    final int REQUEST_READ_EXTERNAL_STORAGE = 5;
    String token;
    String textOfBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("MyLogs", "Check for READ_EXTERNAL_STORAGE permission: " + (permissionCheck == PackageManager.PERMISSION_GRANTED));
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }

        buttonHistory = findViewById(R.id.button_history);
        buttonOpen = findViewById(R.id.button_open);

        @SuppressLint("StaticFieldLeak")
        View.OnClickListener onClickButtonHistory = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        return loadWordlist();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("MyLogs", "Wordlist + " + s);
                    }
                }.execute();

                Toast.makeText(v.getContext(), "Click on history_button", Toast.LENGTH_LONG).show();
            }
        };

        buttonHistory.setOnClickListener(onClickButtonHistory);

        View.OnClickListener onClickButtonOpen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click on open_button", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose file to open"), PICK_FILE_REQUEST);

                //textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                //Log.d("MyLogs", "Count of lines = " + textView.getLineCount());
                //Log.d("MyLogs", "Height = " + textView.getMeasuredHeight());
                //Log.d("MyLogs", "Height of line = " + textView.getLineHeight());

                //Intent intent = new Intent(v.getContext(), ScreenSlidePagerActivity.class);
                //startActivity(ScreenSlidePagerActivity.getIntent(MainActivity.this, textOfBook));
            }
        };

        buttonOpen.setOnClickListener(onClickButtonOpen);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return loadToken();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("MyLogs", "token + " + s);
                token = s;
            }
        }.execute();
    }

    private String parseTokenFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("token");
        } catch (JSONException e) {
            return "Error";
        }
    }

    private String loadToken() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://d6719ff8.ngrok.io/api/tokens");
            String encoding = new String(Base64.encode(("nikita"+":"+"nikita").getBytes("UTF-8"), Base64.DEFAULT));

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);

            Log.d("MyLogs", "Code " + connection.getResponseCode() + "; " + "Message " + connection.getResponseMessage());

            InputStream content = connection.getInputStream();
            BufferedReader in =
                    new BufferedReader (new InputStreamReader (content));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return parseTokenFromJson(stringBuilder.toString());
        } catch(Exception e) {
            e.printStackTrace();
            return "Error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
        try {

            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet("http://d6719ff8.ngrok.io/api/wordlist");
            httpGet.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = httpclient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            Log.d("MyLogs", "Code " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error";
            } else {
                String stringResponse = EntityUtils.toString(response.getEntity());
                parseWordlistFromJson(stringResponse);
                return stringResponse;
            }
        } catch (IOException ex) {
            return "Error " + ex.getMessage();
        }
    }

    private Void addWord(String word) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    return ;
                }

                Uri selectedFileUri = data.getData();
                String path = selectedFileUri.getPath();

                String extension = path.substring(path.lastIndexOf('.'));
                Log.d("MyLogs", "Path: " + path);
                Log.d("MyLogs", "Extension: " + extension);
                String absolutePath = PathUtils.getPath(this, selectedFileUri);
                Log.d("MyLogs", "Absolute path: " + absolutePath);

                EpubReader epubReader = new EpubReader();
                Book book = null;
                try {
                    book = epubReader.readEpub(new FileInputStream(absolutePath));
                } catch (IOException e) {
                    Log.e("MyLogs", e.getMessage());
                }

                if (book != null) {
                    Log.d("MyLogs", book.getTitle());
                    Log.d("MyLogs", "Metadata: " + book.getMetadata().getPublishers());
                    Log.d("MyLogs", String.valueOf(book.getTableOfContents().size()));
                    Log.d("MyLogs", String.valueOf(book.getContents().size()));

                    logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
                    startActivity(ScreenSlidePagerActivity.getIntent(MainActivity.this, textOfBook));
                }
            }
        }
    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            try{
                InputStream is = tocReference.getResource().getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = r.readLine()) != null) {
                    line = Html.fromHtml(line).toString();
                    stringBuilder.append(line + " ");
                    Log.d("Book", line);
                }
                textOfBook = stringBuilder.toString();
            }
            catch(IOException e){

            }

            //logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyLogs", "Permission OK");
            } else {
                Log.d("MyLogs", "Permission FAILED");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
