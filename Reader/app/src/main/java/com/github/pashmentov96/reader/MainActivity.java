package com.github.pashmentov96.reader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonHistory;
    Button buttonOpen;

    final int PICK_FILE_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Log.d("MyLogs", "Path: " + path);
                Log.d("MyLogs", "Data: " + data.getDataString());
                //Toast.makeText(this, "Path: " + path + " + " + data.getDataString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
