package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        welcomeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout askNameView = findViewById(R.id.AskNameView);
                askNameView.setVisibility(View.VISIBLE);
            }
        });

        Button bookBtn = findViewById(R.id.BookBtn);
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(BookActivity.class);
            }
        });
        Button memberBtn = findViewById(R.id.MemberBtn);
        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MemberActivity.class);
            }
        });
        Button loanBtn = findViewById(R.id.LoanBtn);
        loanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(LoanActivity.class);
            }
        });

    }
    private void changeActivity(Class<?> moveTo){
        Intent intent = new Intent(MainActivity.this,moveTo);
        startActivity(intent);
    }
}

