package com.github.pashmentov96.reader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    public void setTextWithWords(List<String> words) {
        String text = getText().toString();
        List<ClickableWord> clickableWords = new ArrayList<>();
        for (String word: words) {
            clickableWords.add(new ClickableWord(word));
        }
        setTextWithClickableWords(text, clickableWords);
    }

    private void setTextWithClickableWords(String text, List<ClickableWord> clickableWords) {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(addClickablePart(text, clickableWords), BufferType.SPANNABLE);
    }

    private SpannableStringBuilder addClickablePart(String str, List<ClickableWord> clickableWords) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        for (ClickableWord clickableWord : clickableWords) {
            int idx1 = str.indexOf(clickableWord.getWord());
            int idx2 = 0;
            while (idx1 != -1) {
                idx2 = idx1 + clickableWord.getWord().length();
                ssb.setSpan(clickableWord.getClickableSpan(), idx1, idx2, 0);
                idx1 = str.indexOf(clickableWord.getWord(), idx2);
            }
        }

        return ssb;
    }

    public static class ClickableWord {
        private String word;
        private ClickableSpan clickableSpan;

        public ClickableWord(final String word) {
            this.word = word;
            this.clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Log.d("MyLogs", "Click on " + word);
                }
            };
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
