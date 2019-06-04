package com.github.pashmentov96.reader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import java.util.List;

public class BookListActivity extends AppCompatActivity implements ViewHolderListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        RecyclerView recyclerView = findViewById(R.id.bookRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 15);

        BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        WorkerHistory workerHistory = new WorkerHistory();
        List<BookInfo> history = workerHistory.parseHistoryFromJsom(this);

        adapter.setBookList(history, this);
    }

    @Override
    public void onBookClicked(String path) {
        openBook(path);
    }

    public void openBook(String path) {
        WorkerTextOfBook workerTextOfBook = new WorkerTextOfBook();
        Pair<String, String> book = workerTextOfBook.getTextOfBook(path);
        if (book != null) {
            String textOfBook = book.second;
            String title = book.first;

            BookInfo bookInfo = new BookInfo(path, title, 0);
            WorkerHistory workerHistory = new WorkerHistory();
            int page = workerHistory.addBook(this, bookInfo);

            startActivity(ScreenSlidePagerActivity.getIntent(this, textOfBook, page));
        }
    }
}
