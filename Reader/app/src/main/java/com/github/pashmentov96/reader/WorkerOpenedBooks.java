package com.github.pashmentov96.reader;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkerOpenedBooks {

    public List<BookInfo> parseHistoryFromJsom(Context context) {
        SomePreferences somePreferences = new SomePreferences(context);
        String json = somePreferences.getVariableHistory();
        List<BookInfo> history = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("books");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject book = jsonArray.getJSONObject(i);
                history.add(new BookInfo(book.getString("link"), book.getString("name"), book.getInt("page")));
            }
        } catch (JSONException e) {
            return null;
        }
        return history;
    }

    public String getJsonFromHistory(List<BookInfo> history) {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (BookInfo book: history) {
            JSONObject jsonBook = new JSONObject();
            try {
                jsonBook.put("link", book.link);
                jsonBook.put("name", book.name);
                jsonBook.put("page", book.page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonBook);
        }
        try {
            json.put("books", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void addBook(Context context, BookInfo book) {
        List<BookInfo> history = parseHistoryFromJsom(context);
        int position = -1;
        for (int i = 0; i < history.size(); ++i) {
            BookInfo item = history.get(i);
            if (item.equals(book)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            history.remove(position);
        }
        history.add(0, book);
        String json = getJsonFromHistory(history);

        SomePreferences somePreferences = new SomePreferences(context);
        somePreferences.setVariableHistory(json);
    }

}
