package com.example.loginpus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button btnUpdatelocation;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.logout);
        btnUpdatelocation = findViewById(R.id.button2);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intToMain);
            }
        });


        btnUpdatelocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent Toupdate = new Intent(HomeActivity.this, gpsdemo.class);
                startActivity(Toupdate);
            }
        });

    }
       // public void btnUpdatelocation (View view){
        //startActivity(new Intent(this, gpsdemo.class));
    }

