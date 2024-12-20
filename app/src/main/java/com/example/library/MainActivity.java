package com.example.library;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    String SHARED_PREFERENCES_NAME = "LibraryManagement";
    String SAVE_KEY = "username";
    SharedPreferences sharedpref;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button welcomeNameBtn = findViewById(R.id.WelcomeNameBtn);
        LinearLayout askNameView = findViewById(R.id.AskNameView);
        EditText nameText = findViewById(R.id.NameText);
        Switch rememberNameSwitch = findViewById(R.id.RememberNameSwitch);
        Button saveNameBtn = findViewById(R.id.SaveNameBtn);
        LinearLayout mainList = findViewById(R.id.mainList);

        // setup SharedPreferences
        sharedpref = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        userName = sharedpref.getString(SAVE_KEY,null);
        if (userName == null) {
            askNameView.setVisibility(View.VISIBLE);
            mainList.setVisibility(View.GONE);
        } else welcomeNameBtn.setText(userName + "، خوش آمدید");

        welcomeNameBtn.setOnClickListener(v -> {
            if (userName != null) {
                userName = null;
                removeName();
            }
            askNameView.setVisibility(View.VISIBLE);
            mainList.setVisibility(View.GONE);
        });
        saveNameBtn.setOnClickListener(v -> {
            if (nameText.getText().toString().isEmpty()) Toast.makeText(this,"نام اجباری است",Toast.LENGTH_SHORT).show();
            else {
                userName = nameText.getText().toString();
                if (rememberNameSwitch.isChecked()) saveName(userName);
                askNameView.setVisibility(View.GONE);
                mainList.setVisibility(View.VISIBLE);
                welcomeNameBtn.setText(userName + "، خوش آمدید");
                Toast.makeText(this,"موفق",Toast.LENGTH_SHORT).show();
            }
        });

        Button bookBtn = findViewById(R.id.BookBtn);
        bookBtn.setOnClickListener(v -> changeActivity(BookActivity.class));
        Button memberBtn = findViewById(R.id.MemberBtn);
        memberBtn.setOnClickListener(v -> changeActivity(MemberActivity.class));
        Button loanBtn = findViewById(R.id.LoanBtn);
        loanBtn.setOnClickListener(v -> changeActivity(LoanActivity.class));

    }
    private void changeActivity(Class<?> moveTo){
        Intent intent = new Intent(MainActivity.this,moveTo);
        startActivity(intent);
    }
    private void saveName(String name){
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putString(SAVE_KEY,name);
        editor.apply();
    }
    private void removeName(){
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.remove(SAVE_KEY);
        editor.apply();
    }
}



