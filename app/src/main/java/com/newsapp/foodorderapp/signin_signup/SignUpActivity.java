package com.newsapp.foodorderapp.signin_signup;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;

public class SignUpActivity extends AppCompatActivity {
    EditText nameUserNew, phoneNumberNew, passwordNew;
    Button btnSignUpNew;
    ProgressBar progressBarNew;
    ImageView go_back;
    boolean newUserCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        passwordNew = findViewById(R.id.passwordNew);
        phoneNumberNew = findViewById(R.id.phoneNumberNew);
        nameUserNew = findViewById(R.id.nameUserNew);
        btnSignUpNew = findViewById(R.id.btnSignUpNew);

        progressBarNew = findViewById(R.id.progressBarNew);
        go_back = findViewById(R.id.go_back);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        btnSignUpNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignUpActivity.this);
                if (phoneNumberNew.getText().toString().isEmpty() || passwordNew.getText().toString().isEmpty() || nameUserNew.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                } else {

                    if (SessionManagement.isConnectedToInternet(getApplicationContext())) {
                        progressBarNew.setVisibility(View.VISIBLE);
                        btnSignUpNew.setEnabled(false);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumberNew.getText().toString());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    progressBarNew.setVisibility(View.GONE);
                                    btnSignUpNew.setEnabled(true);
                                    if (newUserCreated == false) {
                                        Toast.makeText(getApplicationContext(), "Phone number Already exists", Toast.LENGTH_SHORT).show();
                                    }

                                } else {

                                    UserModel user = new UserModel(nameUserNew.getText().toString(), passwordNew.getText().toString(), "no");
                                    databaseReference.setValue(user);

                                    progressBarNew.setVisibility(View.GONE);
                                    btnSignUpNew.setEnabled(true);
                                    newUserCreated = true;
                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                    finish();

                                    Toast.makeText(getApplicationContext(), "Success Sign Up", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                progressBarNew.setVisibility(View.GONE);
                                btnSignUpNew.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Failed Sign up", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Snackbar.make(view, "Check your connectivity!", Snackbar.LENGTH_SHORT).show();
                    }

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