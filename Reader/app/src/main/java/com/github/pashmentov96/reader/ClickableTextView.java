package com.github.pashmentov96.reader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
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

    final int color = getResources().getColor(R.color.black);

    public void setTextWithWords(List<String> words) {
        String text = getText().toString();
        List<ClickableWord> clickableWords = new ArrayList<>();
        for (String word: words) {
            clickableWords.add(new ClickableWord(word, color));
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

    public void setTextWithAllWords() {
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(addClickablePart(getText().toString()), BufferType.SPANNABLE);
    }

    private SpannableStringBuilder addClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        int idx1 = -1;

        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == '\n' || str.charAt(i) == ' ') {
                int idx2 = i;
                if (idx1 != -1) {
                    ClickableWord clickableWord = new ClickableWord(str.substring(idx1, idx2), color);
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
            ClickableWord clickableWord = new ClickableWord(str.substring(idx1, idx2), color);
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
        public ClickableWord(final String word, final int color) {
            this.word = word;
            this.clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Log.d("MyLogs", "Click on " + word);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    //super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(color);
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