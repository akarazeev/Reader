package com.github.pashmentov96.reader;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClickableTextView extends android.support.v7.widget.AppCompatTextView {
    public ClickableTextView(Context context) {
        super(context);
    }

    public ClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    final int color = getResources().getColor(R.color.black);

    public void setTextWithAllWords(TextView translationOfWord) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(addClickablePart(getText().toString(), translationOfWord), BufferType.SPANNABLE);
    }

    private SpannableStringBuilder addClickablePart(String str, TextView translationOfWord) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        int idx1 = -1;

        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '\n' || str.charAt(i) == ' ') {
                int idx2 = i;
                if (idx1 != -1) {
                    ClickableWord clickableWord = new ClickableWord(str.substring(idx1, idx2), color, translationOfWord);
                    ssb.setSpan(clickableWord.getClickableSpan(), idx1, idx2, 0);
                }
                idx1 = -1;
            } else {
                if (idx1 == -1) {
                    idx1 = i;
                }
            }
        }

        if (idx1 != -1) {
            int idx2 = str.length();
            ClickableWord clickableWord = new ClickableWord(str.substring(idx1, idx2), color, translationOfWord);
            ssb.setSpan(clickableWord.getClickableSpan(), idx1, idx2, 0);
        }

        return ssb;
    }

    @Override
    public void scrollTo(int x, int y) {
        //super.scrollTo(x, y);
    }

    public static class ClickableWord {
        private String word;
        private ClickableSpan clickableSpan;

        public ClickableWord(final String word, final int color, final TextView translationOfWord) {
            this.word = word;
            this.clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    ScreenSlidePagerActivity.clickedWord = word;
                    Log.d("MyLogs", "Click on " + word);
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPostExecute(String s) {
                            translationOfWord.setText(parseFromJson(s));
                            Log.d("MyLogs", "json = " + s);
                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            return translate(word);
                        }
                    }.execute();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(color);
                }
            };
        }

        private String parseFromJson(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.getJSONArray("text").get(0).toString();
            } catch (JSONException e) {
                return "Error";
            }
        }

        private String translate(String word) {
            String key = "trnsl.1.1.20190531T071741Z.3ddfb9a0422e3948.53a88c2bd5fb9d0ef621d39e03e76ca81ddaf681";
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?lang=en-ru&key=" + key + "&text=" + word);
                Log.d("MyLogs", "URL = " + url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                Log.d("MyLogs", "Code " + connection.getResponseCode() + "; " + "Message " + connection.getResponseMessage());
                InputStream content = connection.getInputStream();
                BufferedReader in =
                        new BufferedReader (new InputStreamReader(content));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();
            } catch(Exception e) {
                e.printStackTrace();
                return "Error";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }


        /**
         * @return the word
         */
        public String getWord() {
            return word;
        }

        /**
         * @return the clickableSpan
         */
        public ClickableSpan getClickableSpan() {
            return clickableSpan;
        }
    }
}
