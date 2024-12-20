package com.example.library;

public class Book {
    private String _name;
    private String _writer;
    private int _year;
    private String _shabak;

    public Book(String name, String writer, int year, String shabak){
        _name = name;
        _writer = writer;
        _year = year;
        _shabak = shabak;
    }
    public String getName(){
        return _name;
    }
    public String getWriter(){
        return _writer;
    }
    public int getYear(){
        return _year;
    }
    public String getShabak(){
        return _shabak;
    }
}
