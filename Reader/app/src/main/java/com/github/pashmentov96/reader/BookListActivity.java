package com.github.pashmentov96.reader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class BookListActivity extends AppCompatActivity {
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

        WorkerOpenedBooks workerOpenedBooks = new WorkerOpenedBooks();
        List<BookInfo> history = workerOpenedBooks.parseHistoryFromJsom(this);

        adapter.setBookList(history);
    }
}
