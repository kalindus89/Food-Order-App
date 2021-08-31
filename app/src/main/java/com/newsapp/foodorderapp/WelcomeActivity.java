package com.newsapp.foodorderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.newsapp.foodorderapp.all_foods_home.HomeActivity;
import com.newsapp.foodorderapp.signin_signup.SignInActivity;
import com.newsapp.foodorderapp.signin_signup.SignUpActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnSignUp, btnSignIn;

    @Override
    protected void onStart() {
        super.onStart();

        if (new SessionManagement().isLogin(this) == true) {
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SessionManagement.isConnectedToInternet(WelcomeActivity.this)) {

                    Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(view, "Check your connectivity!", Snackbar.LENGTH_SHORT).show();
                }

            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SessionManagement.isConnectedToInternet(WelcomeActivity.this)) {

                    Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(view, "Check your connectivity!", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }
}