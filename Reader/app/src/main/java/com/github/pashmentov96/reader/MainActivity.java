package com.github.pashmentov96.reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {

    Button buttonWordlist;
    Button buttonOpen;

    final int PICK_FILE_REQUEST = 10;
    final int REQUEST_READ_EXTERNAL_STORAGE = 5;
    final int OPEN_LOGIN_ACTIVITY = 20;

    String textOfBook;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SomePreferences somePreferences = new SomePreferences(this);
        MenuInflater inflater = getMenuInflater();
        if (somePreferences.getVariableIsLogged() == 0) {
            inflater.inflate(R.menu.menu_notlogged, menu);
        } else {
            inflater.inflate(R.menu.menu_logged, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_login:
                Toast.makeText(this, "login", Toast.LENGTH_LONG).show();
                clickOnLoginButton();
                return true;
            case R.id.action_logout:
                Toast.makeText(this, "logout", Toast.LENGTH_LONG).show();
                clickOnLogoutButton();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clickOnLoginButton() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, OPEN_LOGIN_ACTIVITY);
    }

    private void clickOnLogoutButton() {
        SomePreferences somePreferences = new SomePreferences(this);
        somePreferences.setVariableIsLogged(0);
        somePreferences.setToken("Error");
        recreate();
    }

    private void clickOnOpenButton() {
        SomePreferences somePreferences = new SomePreferences(this);
        if (somePreferences.getVariableIsLogged() == 0) {
            Toast.makeText(this, "You should be logged to open book", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.setType("application/epub+zip");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Choose file to open"), PICK_FILE_REQUEST);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void clickOnWordlistButton(View view) {
        SomePreferences somePreferences = new SomePreferences(this);
        if (somePreferences.getVariableIsLogged() == 0) {
            Toast.makeText(this, "You should be logged to open your wordlist", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, WordlistActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SomePreferences somePreferences = new SomePreferences(this);

        Log.d("MyLogs", "IsLogged = " + somePreferences.getVariableIsLogged());

        Toolbar myToolBar = findViewById(R.id.my_toolbar_2);
        setSupportActionBar(myToolBar);

        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("MyLogs", "Check for READ_EXTERNAL_STORAGE permission: " + (permissionCheck == PackageManager.PERMISSION_GRANTED));
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }

        buttonWordlist = findViewById(R.id.button_wordlist);
        buttonOpen = findViewById(R.id.button_open);

        @SuppressLint("StaticFieldLeak")
        View.OnClickListener onClickButtonWordlist = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnWordlistButton(v);
            }
        };

        buttonWordlist.setOnClickListener(onClickButtonWordlist);

        View.OnClickListener onClickButtonOpen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click on open_button", Toast.LENGTH_LONG).show();
                clickOnOpenButton();
            }
        };

        buttonOpen.setOnClickListener(onClickButtonOpen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    return ;
                }

                Uri selectedFileUri = data.getData();
                String path = selectedFileUri.getPath();
                Log.d("MyLogs", "Type = " + data.getType());

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
            if (requestCode == OPEN_LOGIN_ACTIVITY) {
                if (data == null) {
                    return ;
                }
                SomePreferences somePreferences = new SomePreferences(this);
                somePreferences.setVariableIsLogged(1);
                somePreferences.setToken(data.getStringExtra("token"));
                recreate();
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
