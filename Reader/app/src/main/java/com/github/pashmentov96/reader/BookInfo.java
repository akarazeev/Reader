package com.github.pashmentov96.reader;

public class BookInfo {
    public String link;
    public String name;
    public int page;

    public BookInfo(String _link, String _name, int _page) {
        link = _link;
        name = _name;
        page = _page;
    }

    public boolean equals(BookInfo book) {
        return book.link.equals(link);
    }

    @Override
    public String toString() {
        return "{\"link\": " + link + ", \"name\": " + name + ", \"page\": " + page + "}";
    }
}
