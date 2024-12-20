package com.example.library;
public class Member {
    private String _fullName;
    private String _codeMeli;
    private boolean _canRent;

    public Member(String fullName, String codeMeli, boolean canRent){
        _fullName = fullName;
        _codeMeli = codeMeli;
        _canRent = canRent;
    }

    public String getFullName(){
        return _fullName;
    }
    public String getCodeMeli(){
        return _codeMeli;
    }
    public boolean getCanRent(){
        return _canRent;
    }
}

