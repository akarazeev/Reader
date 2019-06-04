package com.github.pashmentov96.reader;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<BookInfo> bookList = new ArrayList<>();

    public void setBookList(List<BookInfo> history) {
        bookList = history;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_item_view, viewGroup, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder bookViewHolder, int i) {
        BookInfo currentBook = bookList.get(i);
        bookViewHolder.id = i;
        bookViewHolder.name.setText(currentBook.name);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private int id;

        public BookViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameOfBook);
            View bookItemView = itemView.findViewById(R.id.bookItemView);
            bookItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MyLogs", "Id in Listener " + id);
                }
            });
        }
    }
}