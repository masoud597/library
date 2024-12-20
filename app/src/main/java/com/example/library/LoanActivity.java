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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoanActivity extends AppCompatActivity {
    EditText memberCodeMeli;
    EditText bookShabak;
    EditText loanDate;
    EditText loanTahvil;
    ListView loanList;
    SQLiteDatabase db;
    ArrayList<Loan> loans;
    DatabaseHelper sqliteHelper;
    boolean inEditMode = false;
    Loan itemInEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan);
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
        memberCodeMeli = findViewById(R.id.memberCodeMeli);
        bookShabak = findViewById(R.id.bookShabak);
        loanDate = findViewById(R.id.LoanDate);
        loanTahvil = findViewById(R.id.LoanTahvil);
        loanList = findViewById(R.id.LoanList);

        // get items from database and show them in ListView
        sqliteHelper = new DatabaseHelper(getApplicationContext());
        db = sqliteHelper.getWritableDatabase();
        setupList();


        // handle button clicks
        newItemBtn.setOnClickListener(v -> {
            newFormView.setVisibility(View.VISIBLE);
            inEditMode = false;
            memberCodeMeli.setEnabled(true);
            bookShabak.setEnabled(true);

            // set current date
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String strDate = formatter.format(date);
            loanDate.setText(strDate);
        });
        backBtn.setOnClickListener(v -> {
            newFormView.setVisibility(View.GONE);
            OptionBtnsView.setVisibility(View.GONE);
            clearInputs();
        });
        saveBtn.setOnClickListener(v -> {
            String result;
            if (inEditMode) {
                // for when editing an item
                ContentValues values = new ContentValues();
                values.put("id", itemInEdit.getCodeAmant());
                values.put("codemeli", itemInEdit.getCodeMeli());
                values.put("shabak", itemInEdit.getShabak());
                values.put("tarikh", itemInEdit.getTarikh());
                values.put("tahvil", loanTahvil.getText().toString());
                result = sqliteHelper.updateRecord("loans",values,"id",String.valueOf(itemInEdit.getCodeAmant())) > 0 ? "موفق" : "خطا";
            }else {
                // for when adding an item
                Member user = sqliteHelper.getMember(memberCodeMeli.getText().toString());
                if (user != null){
                    if (user.getCanRent()){
                        if (sqliteHelper.getBook(bookShabak.getText().toString()) != null) {
                            int loanTahvilParsed;
                            try {
                                loanTahvilParsed = Integer.parseInt(loanTahvil.getText().toString());
                            }catch (NumberFormatException e){
                                loanTahvilParsed = 0;
                            }
                            Loan newLoan = new Loan(sqliteHelper.getNextLoanId(), memberCodeMeli.getText().toString(),bookShabak.getText().toString(),loanDate.getText().toString(), loanTahvilParsed);
                            result = sqliteHelper.addLoan(newLoan) > -1 ? "موفق" : "خطا";
                        }else{result = "کتاب با این شابک وجود ندارد";}
                    }else{result = "عضو اجازه امانت ندارد";}
                }else{result = "عضو با این کدملی وجود ندارد";}
            }
            newFormView.setVisibility(View.GONE);
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
            setupList();
            OptionBtnsView.setVisibility(View.GONE);
            memberCodeMeli.setEnabled(true);
            bookShabak.setEnabled(true);
            clearInputs();
        });
        loanList.setOnItemClickListener((parent, view, position, id) -> {
            inEditMode = true;
            memberCodeMeli.setEnabled(false);
            bookShabak.setEnabled(false);
            newFormView.setVisibility(View.VISIBLE);
            itemInEdit = loans.get(position);
            memberCodeMeli.setText(itemInEdit.getCodeMeli());
            bookShabak.setText(itemInEdit.getShabak());
            loanDate.setText(itemInEdit.getTarikh());
            loanTahvil.setText(String.valueOf(loans.get(position).getTahvil()));
            OptionBtnsView.setVisibility(View.VISIBLE);
            setupList();
        });
        deleteRecordBtn.setOnClickListener(v -> {
            inEditMode = false;
            newFormView.setVisibility(View.GONE);
            sqliteHelper.deleteRecord("loans","id",String.valueOf(itemInEdit.getCodeAmant()));
            setupList();
            clearInputs();
        });
    }
    private void clearInputs(){
        memberCodeMeli.setText("");
        bookShabak.setText("");
        loanDate.setText("");
        loanTahvil.setText("");
    }
    private void setupList(){
        loans = sqliteHelper.getLoans();
        // setup how to show items in ListView
        ArrayList<Map<String,String>> loanToShowList = new ArrayList<>();
        String[] from = {"title", "subtitle"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        loans.forEach(item -> {
            Map<String, String> itemToShow = new HashMap<>();
            itemToShow.put("title",item.getCodeAmant() + " - امانت در تاریخ '" + item.getTarikh() + "' به مدت '" + item.getTahvil() + "' روز");
            itemToShow.put("subtitle", "کدملی: '" + item.getCodeMeli() + "' شابک: '" + item.getShabak() + "'");
            loanToShowList.add(itemToShow);
        });
        SimpleAdapter adapter = new SimpleAdapter(this, loanToShowList, android.R.layout.simple_list_item_2,from,to);
        loanList.setAdapter(adapter);
    }
}