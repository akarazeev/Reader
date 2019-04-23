package com.github.pashmentov96.reader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonHistory;
    Button buttonOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHistory = (Button)findViewById(R.id.button_history);
        buttonOpen = (Button)findViewById(R.id.button_open);

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
            }
        };

        buttonOpen.setOnClickListener(onClickButtonOpen);


    }
}
