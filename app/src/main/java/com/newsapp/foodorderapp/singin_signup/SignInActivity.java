package com.newsapp.foodorderapp.singin_signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.home.HomeActivity;
import com.newsapp.foodorderapp.R;

public class SignInActivity extends AppCompatActivity {

    EditText phoneNumber, password;
    Button btnSignIn;
    ProgressBar progressBar;
    LinearLayout goToSingUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        password = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phoneNumber);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progressBar);
        goToSingUp = findViewById(R.id.goToSingUp);

        goToSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignInActivity.this);
                if (phoneNumber.getText().toString().isEmpty() || password .getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    btnSignIn.setEnabled(false);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber.getText().toString());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                UserModel user = snapshot.getValue(UserModel.class);

                                if (user.getPassword().equals(password.getText().toString())) {

                                    progressBar.setVisibility(View.GONE);
                                    btnSignIn.setEnabled(true);

                                    new SessionManagement().setUserName(SignInActivity.this,phoneNumber.getText().toString());
                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    intent.putExtra("name",user.getName());
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    btnSignIn.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                btnSignIn.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Wrong Username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            progressBar.setVisibility(View.GONE);
                            btnSignIn.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Failed Login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}