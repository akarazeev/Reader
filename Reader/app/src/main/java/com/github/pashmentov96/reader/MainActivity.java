package com.github.pashmentov96.reader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class MainActivity extends AppCompatActivity {

    Button buttonHistory;
    Button buttonOpen;

    final int PICK_FILE_REQUEST = 10;
    final int REQUEST_READ_EXTERNAL_STORAGE = 5;

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

        View.OnClickListener onClickButtonHistory = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        };

        buttonOpen.setOnClickListener(onClickButtonOpen);


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
                }
                Log.d("MyLogs", "Data: " + data.getDataString());
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
