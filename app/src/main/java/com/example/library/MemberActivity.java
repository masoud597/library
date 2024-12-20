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
import android.widget.Switch;
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

public class MemberActivity extends AppCompatActivity {
    EditText memberFNameTxt;
    EditText memberIDTxt;
    Switch memberCanRentSwitch;
    ListView memberList;
    SQLiteDatabase db;
    ArrayList<Member> members;
    DatabaseHelper sqliteHelper;
    boolean inEditMode = false;
    Member itemInEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_member);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
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
        memberFNameTxt = findViewById(R.id.memberCodeMeli);
        memberIDTxt = findViewById(R.id.LoanTahvil);
        memberList = findViewById(R.id.LoanList);
        memberCanRentSwitch = findViewById(R.id.CanRentSwitch);

        // get items from database and show them in ListView
        sqliteHelper = new DatabaseHelper(getApplicationContext());
        db = sqliteHelper.getWritableDatabase();
        setupList();


        // handle button clicks
        newItemBtn.setOnClickListener(v -> {
            newFormView.setVisibility(View.VISIBLE);
            inEditMode = false;
            memberIDTxt.setEnabled(true);
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
                values.put("fullname", memberFNameTxt.getText().toString());
                values.put("codemeli", itemInEdit.getCodeMeli());
                values.put("canrent",memberCanRentSwitch.isChecked());
                result = sqliteHelper.updateRecord("members",values,"codemeli",itemInEdit.getCodeMeli()) > 0 ? "موفق" : "خطا";
            }else {
                Member newMember = new Member(memberFNameTxt.getText().toString(), memberIDTxt.getText().toString(), memberCanRentSwitch.isChecked());
                result = sqliteHelper.addMember(newMember) > -1 ? "موفق" : "خطا";
            }
            newFormView.setVisibility(View.GONE);
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
            setupList();
            OptionBtnsView.setVisibility(View.GONE);
            memberIDTxt.setEnabled(true);
            clearInputs();
        });
        memberList.setOnItemClickListener((parent, view, position, id) -> {
            inEditMode = true;
            memberIDTxt.setEnabled(false);
            newFormView.setVisibility(View.VISIBLE);
            itemInEdit = members.get(position);
            memberFNameTxt.setText(members.get(position).getFullName());
            memberIDTxt.setText(members.get(position).getCodeMeli());
            memberCanRentSwitch.setChecked(members.get(position).getCanRent());
            OptionBtnsView.setVisibility(View.VISIBLE);
            setupList();
        });
        deleteRecordBtn.setOnClickListener(v -> {
            inEditMode = false;
            newFormView.setVisibility(View.GONE);
            sqliteHelper.deleteRecord("members","codemeli",itemInEdit.getCodeMeli());
            setupList();
            clearInputs();
        });
    }
    private void clearInputs(){
        memberFNameTxt.setText("");
        memberIDTxt.setText("");
    }
    private void setupList(){
        members = sqliteHelper.getMembers();
        // setup how to show items in ListView
        ArrayList<Map<String,String>> memberToShowList = new ArrayList<>();
        String[] from = {"title", "subtitle"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        members.forEach(item -> {
            Map<String, String> itemToShow = new HashMap<>();
            itemToShow.put("title","نام و نام خانوادگی : " + item.getFullName());
            itemToShow.put("subtitle", "کد ملی : " + item.getCodeMeli() + ", اجازه امانت " + (item.getCanRent() ? "دارد" : "ندارد"));
            memberToShowList.add(itemToShow);
        });
        SimpleAdapter adapter = new SimpleAdapter(this, memberToShowList, android.R.layout.simple_list_item_2,from,to);
        memberList.setAdapter(adapter);
    }
}