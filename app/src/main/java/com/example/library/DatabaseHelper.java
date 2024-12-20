package com.example.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE books (id INTEGER, name TEXT, writer TEXT, year INTEGER, shabak TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE members (id INTEGER, fullname TEXT, codemeli TEXT PRIMARY KEY, canrent INTEGER)");
        db.execSQL("CREATE TABLE loans (id INTEGER PRIMARY KEY, codemeli TEXT, shabak TEXT, tarikh TEXT, tahvil INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long addBook(Book book) {
        ContentValues values = new ContentValues();
        values.put("name", book.getName());
        values.put("writer", book.getWriter());
        values.put("year", book.getYear());
        values.put("shabak", book.getShabak());
        return db.insert("books", null, values);
    }

    public ArrayList<Book> getBooks() {
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = db.query("books", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Book book = new Book(cursor.getString(cursor.getColumnIndexOrThrow("name")), cursor.getString(cursor.getColumnIndexOrThrow("writer")), cursor.getInt(cursor.getColumnIndexOrThrow("year")), cursor.getString(cursor.getColumnIndexOrThrow("shabak")));
                books.add(book);
            }
            cursor.close();
        }
        return books;
    }

    public long addMember(Member member) {
        ContentValues values = new ContentValues();
        values.put("fullname", member.getFullName());
        values.put("codemeli", member.getCodeMeli());
        values.put("canrent", member.getCanRent() ? 1 : 0);

        return db.insert("members", null, values);

    }

    public ArrayList<Member> getMembers() {
        ArrayList<Member> members = new ArrayList<>();
        Cursor cursor = db.query("members", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Member member = new Member(cursor.getString(cursor.getColumnIndexOrThrow("fullname")), cursor.getString(cursor.getColumnIndexOrThrow("codemeli")), cursor.getInt(cursor.getColumnIndexOrThrow("canrent")) > 0);
                members.add(member);
            }
            cursor.close();
        }
        return members;
    }

    public long addLoan(Loan loan) {
        ContentValues values = new ContentValues();
        values.put("codemeli", loan.getCodeMeli());
        values.put("shabak", loan.getShabak());
        values.put("tarikh", loan.getTarikh().toString());
        values.put("tahvil", loan.getTahvil());
        return db.insert("loans", null, values);
    }

    public ArrayList<Loan> getLoans() {
        ArrayList<Loan> loans = new ArrayList<>();
        Cursor cursor = db.query("loans", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Loan loan = new Loan(cursor.getInt(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("codemeli")), cursor.getString(cursor.getColumnIndexOrThrow("shabak")), cursor.getString(cursor.getColumnIndexOrThrow("tarikh")), cursor.getInt(cursor.getColumnIndexOrThrow("tahvil")));
                loans.add(loan);
            }
            cursor.close();
        }
        return loans;
    }

    public Member getMember(String codeMeli) {
        Member result = null;
        String[] columns = {"codemeli, canrent"};
        Cursor cursor = db.query("members", columns, "codemeli = ?", new String[]{String.valueOf(codeMeli)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = new Member("", cursor.getString(cursor.getColumnIndexOrThrow("codemeli")), cursor.getInt(cursor.getColumnIndexOrThrow("canrent")) > 0);
            }
            cursor.close();
        }
        return result;
    }

    public Book getBook(String shabak) {
        Book result = null;
        String[] columns = {"shabak"};
        Cursor cursor = db.query("books", columns, "shabak = ?", new String[]{String.valueOf(shabak)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = new Book("", "", 0, cursor.getString(cursor.getColumnIndexOrThrow("shabak")));
            }
            cursor.close();
        }
        return result;
    }

    public int updateRecord(String tableName, ContentValues values, String whereKey, String whereValue) {
        return db.update(tableName, values, whereKey + " = ?", new String[]{String.valueOf(whereValue)});
    }

    public void deleteRecord(String tableName, String whereKey, String whereValue) {
        db.delete(tableName, whereKey + " = ?", new String[]{String.valueOf(whereValue)});
    }

    public int getNextLoanId() {
        String query = "SELECT MAX(id) FROM loans";
        Cursor cursor = db.rawQuery(query, null);
        int maxId = -1;
        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }
        cursor.close();
        return maxId+1;
    }
}
