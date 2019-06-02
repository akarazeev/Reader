package com.github.pashmentov96.reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
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
import java.util.List;
import java.util.Locale;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {

    Button buttonWordlist;
    Button buttonOpen;
    Button buttonRecentBooks;

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
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_login:
                clickOnLoginButton();
                return true;
            case R.id.action_logout:
                clickOnLogoutButton();
                return true;
            case R.id.action_about:
                clickOnAboutButton();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clickOnAboutButton() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
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
            Toast.makeText(this, getResources().getString(R.string.must_be_logged_book), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.setType("application/epub+zip");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.choose_file)), PICK_FILE_REQUEST);
        }
    }

    private void clickOnRecentBooksButton() {
        WorkerOpenedBooks workerOpenedBooks = new WorkerOpenedBooks();
        List<BookInfo> history = workerOpenedBooks.parseHistoryFromJsom(this);
        for (BookInfo book: history) {
            Log.d("history", book.toString());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void clickOnWordlistButton(View view) {
        SomePreferences somePreferences = new SomePreferences(this);
        if (somePreferences.getVariableIsLogged() == 0) {
            Toast.makeText(this, getResources().getString(R.string.must_be_logged_wordlist), Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, WordlistActivity.class));
        }
    }

    public void setLocale(String localeName) {
        SomePreferences somePreferences = new SomePreferences(this);
        String prevLocale = getResources().getConfiguration().locale.toString();
        if (prevLocale.contains("en")) {
            prevLocale = "en";
        }
        if (!localeName.equals(prevLocale)) {
            Locale myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
            somePreferences.setVariableLanguage(localeName);
            recreate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SomePreferences somePreferences = new SomePreferences(this);

        Log.d("MyLogs", "Language = " + somePreferences.getVariableLanguage());

        Log.d("MyLogs", "Locale = " + getResources().getConfiguration().locale);
        Log.d("MyLogs", "User's locale = " + (new Locale(somePreferences.getVariableLanguage())));
        setLocale(somePreferences.getVariableLanguage());

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
        buttonRecentBooks = findViewById(R.id.buttonRecentBooks);

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

        final View.OnClickListener onClickButtonRecentBooks = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnRecentBooksButton();
            }
        };
        buttonRecentBooks.setOnClickListener(onClickButtonRecentBooks);
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


                WorkerTextOfBook workerTextOfBook = new WorkerTextOfBook();
                Pair<String, String> book = workerTextOfBook.getTextOfBook(absolutePath);
                if (book != null) {
                    textOfBook = book.second;
                    String title = book.first;

                    BookInfo bookInfo = new BookInfo(absolutePath, title, 0);
                    WorkerOpenedBooks workerOpenedBooks = new WorkerOpenedBooks();
                    workerOpenedBooks.addBook(this, bookInfo);

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
