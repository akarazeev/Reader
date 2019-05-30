package com.github.pashmentov96.reader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView noRegistration = findViewById(R.id.hint_registration);
        noRegistration.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textWrongLogin = findViewById(R.id.textWrongLogin);
        textWrongLogin.setVisibility(View.INVISIBLE);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(final View v) {
        final TextView login = findViewById(R.id.login);
        final TextView password = findViewById(R.id.password);

        final String textLogin = login.getText().toString();
        final String textPassword = password.getText().toString();
        final TextView textWrongLogin = findViewById(R.id.textWrongLogin);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return loadToken(textLogin, textPassword);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("MyLogs", "token + " + s);
                if (s.equals("Error")) {
                    textWrongLogin.setVisibility(View.VISIBLE);
                    login.setText("");
                    password.setText("");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("token", s);
                    setResult(RESULT_OK, intent);
                    finish();
                }
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

    private String loadToken(String login, String password) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://d6719ff8.ngrok.io/api/tokens");
            String encoding = new String(Base64.encode((login + ":" + password).getBytes("UTF-8"), Base64.DEFAULT));

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);

            Log.d("MyLogs", "Code " + connection.getResponseCode() + "; " + "Message " + connection.getResponseMessage());

            InputStream content = connection.getInputStream();
            BufferedReader in =
                    new BufferedReader (new InputStreamReader(content));

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

}
