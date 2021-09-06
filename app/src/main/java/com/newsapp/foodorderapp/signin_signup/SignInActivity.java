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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.protobuf.StringValue;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.WelcomeActivity;
import com.newsapp.foodorderapp.all_foods_home.HomeActivity;
import com.newsapp.foodorderapp.R;

import java.util.HashMap;
import java.util.Map;

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
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignInActivity.this);
                if (phoneNumber.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                } else {

                    if (SessionManagement.isConnectedToInternet(SignInActivity.this)) {

                        progressBar.setVisibility(View.VISIBLE);
                        btnSignIn.setEnabled(false);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber.getText().toString());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    UserModel user = snapshot.getValue(UserModel.class);

                                    if (user.getPassword().equals(password.getText().toString())) {
                                        new SessionManagement().setUserName(SignInActivity.this, phoneNumber.getText().toString(), user.getName(), "yes");
                                        updateFirebaseToken(user);

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
                    } else {
                        Snackbar.make(view, "Check your connectivity!", Snackbar.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    private void updateFirebaseToken(UserModel user) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "token receive failed", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        String refreshToken = task.getResult();

                        // Toast.makeText(getApplicationContext(), refreshToken, Toast.LENGTH_SHORT).show();


                        DocumentReference nycRef = FirebaseFirestore.getInstance().document("FoodOrders/" + new SessionManagement().getPhone(getApplicationContext()));
                        nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("messagingToken", refreshToken);

                                    nycRef.update(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            goHomeActivity(refreshToken,user);

                                        }
                                    });

                                } else {
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("totalAmount", 0);
                                    note.put("numberOfOrders", 0);
                                    note.put("messagingToken", refreshToken);
                                    nycRef.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            goHomeActivity(refreshToken,user);
                                        }
                                    });

                                }

                            }
                        });

                    }
                });

    }

    public void goHomeActivity(String fbToken, UserModel model) {


        progressBar.setVisibility(View.GONE);
        btnSignIn.setEnabled(true);

        new SessionManagement().setFBToken(SignInActivity.this, fbToken);

        if(model.getSubscribeState().equals("no")){
            new SessionManagement().setState(SignInActivity.this,"no");
        }else {
            new SessionManagement().setState(SignInActivity.this,"yes");
        }

        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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