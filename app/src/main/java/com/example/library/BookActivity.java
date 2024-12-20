package com.example.library;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookActivity extends AppCompatActivity {
    EditText bookName;
    EditText bookWriter;
    EditText bookYear;
    EditText bookShabak;
    ListView bookList;
    SQLiteDatabase db;
    ArrayList<Book> books;
    DatabaseHelper sqliteHelper;
    boolean inEditMode = false;
    Book itemInEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // get ui components
        Button saveBtn = findViewById(R.id.SaveBtn);
        Button backBtn = findViewById(R.id.BackBtn);
        Button deleteRecordBtn = findViewById(R.id.DeleteRecordBtn);
        FloatingActionButton newItemBtn = findViewById(R.id.NewItemFloatBtn);
        LinearLayout newFormView = findViewById(R.id.NewFormView);
        LinearLayout OptionBtnsView = findViewById(R.id.OptionBtnsView);
        bookName = findViewById(R.id.memberCodeMeli);
        bookWriter = findViewById(R.id.bookShabak);
        bookYear = findViewById(R.id.LoanDate);
        bookShabak = findViewById(R.id.LoanTahvil);
        bookList = findViewById(R.id.LoanList);

        // get items from database and show them in ListView
        sqliteHelper = new DatabaseHelper(getApplicationContext());
        db = sqliteHelper.getWritableDatabase();
        setupList();


        // handle button clicks
        newItemBtn.setOnClickListener(v -> {
            newFormView.setVisibility(View.VISIBLE);
            inEditMode = false;
            bookShabak.setEnabled(true);
        });
        backBtn.setOnClickListener(v -> {
            newFormView.setVisibility(View.GONE);
            OptionBtnsView.setVisibility(View.GONE);
            clearInputs();
        });
        saveBtn.setOnClickListener(v -> {
            String result;
            if (inEditMode) {
                ContentValues values = new ContentValues();
                values.put("name", bookName.getText().toString());
                values.put("writer", bookWriter.getText().toString());
                values.put("year", bookYear.getText().toString());
                values.put("shabak", itemInEdit.getShabak());
                result = sqliteHelper.updateRecord("books",values,"shabak",itemInEdit.getShabak()) > 0 ? "موفق" : "خطا";
            }else {
                int bookYearParsed;
                try {
                    bookYearParsed = Integer.parseInt(bookYear.getText().toString());
                }catch (NumberFormatException e){
                    bookYearParsed = 0;
                }
                if (!bookShabak.getText().toString().isEmpty()) {
                    Book newBook = new Book(bookName.getText().toString(), bookWriter.getText().toString(), bookYearParsed, bookShabak.getText().toString());
                    result = sqliteHelper.addBook(newBook) > -1 ? "موفق" : "خطا";
                }else{result = "شابک نمی تواند خالی باشد";}
            }
            newFormView.setVisibility(View.GONE);
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
            setupList();
            OptionBtnsView.setVisibility(View.GONE);
            bookShabak.setEnabled(true);
            clearInputs();
        });
        bookList.setOnItemClickListener((parent, view, position, id) -> {
            inEditMode = true;
            bookShabak.setEnabled(false);
            newFormView.setVisibility(View.VISIBLE);
            itemInEdit = books.get(position);
            bookName.setText(books.get(position).getName());
            bookWriter.setText(books.get(position).getWriter());
            bookYear.setText(String.valueOf(books.get(position).getYear()));
            bookShabak.setText(books.get(position).getShabak());
            OptionBtnsView.setVisibility(View.VISIBLE);
            setupList();
        });
        deleteRecordBtn.setOnClickListener(v -> {
            inEditMode = false;
            newFormView.setVisibility(View.GONE);
            sqliteHelper.deleteRecord("books","shabak",itemInEdit.getShabak());
            setupList();
            clearInputs();
        });
    }
    private void clearInputs(){
        bookName.setText("");
        bookWriter.setText("");
        bookYear.setText("");
        bookShabak.setText("");
    }
    private void setupList(){
        books = sqliteHelper.getBooks();
        // setup how to show items in ListView
        ArrayList<Map<String,String>> bookToShowList = new ArrayList<>();
        String[] from = {"title", "subtitle"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        books.forEach(item -> {
            Map<String, String> itemToShow = new HashMap<>();
            itemToShow.put("title","کتاب '" + item.getName() + "' توسط '" + item.getWriter() + "'");
            itemToShow.put("subtitle", "با شابک '" + item.getShabak() + "' در سال '" + item.getYear() + "'");
            bookToShowList.add(itemToShow);
        });
        SimpleAdapter adapter = new SimpleAdapter(this, bookToShowList, android.R.layout.simple_list_item_2,from,to);
        bookList.setAdapter(adapter);
    }
}