package com.example.library;

import java.util.Date;

public class Loan {
    private int _codeAmant;
    private String _codeMeli;
    private String _shabak;
    private String _tarikh;
    private int _tahvil;

    public Loan(int codeAmant, String codeMeli, String shabak, String tarikh, int tahvil){
        _codeAmant = codeAmant;
        _codeMeli = codeMeli;
        _shabak = shabak;
        _tarikh = tarikh;
        _tahvil = tahvil;
    }
    public int getCodeAmant(){
        return _codeAmant;
    }
    public String getCodeMeli(){
        return _codeMeli;
    }
    public String getShabak(){
        return _shabak;
    }
    public String getTarikh(){
        return _tarikh;
    }
    public int getTahvil(){
        return _tahvil;
    }
}
