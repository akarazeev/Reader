package com.github.pashmentov96.reader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView login = findViewById(R.id.login);
        TextView password = findViewById(R.id.password);
        Intent intent = new Intent();
        intent.putExtra("login", login.getText().toString());
        intent.putExtra("password", password.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
