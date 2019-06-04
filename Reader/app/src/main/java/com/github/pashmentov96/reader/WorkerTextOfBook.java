package com.github.pashmentov96.reader;

import android.text.Html;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class WorkerTextOfBook {
    private String textOfBook;

    public Pair<String, String> getTextOfBook(String path) {
        EpubReader epubReader = new EpubReader();
        Book book = null;
        try {
            book = epubReader.readEpub(new FileInputStream(path));
        } catch (IOException e) {
            Log.e("MyLogs", e.getMessage());
        }

        if (book != null) {
            Log.d("MyLogs", book.getTitle());
            Log.d("MyLogs", "Metadata: " + book.getMetadata().getTitles());

            List<String> titles = book.getMetadata().getTitles();
            if (titles.size() == 0) {
                titles.add("Without name");
            }

            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
            return Pair.create(titles.get(0), textOfBook);
        }
        return null;
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
}
