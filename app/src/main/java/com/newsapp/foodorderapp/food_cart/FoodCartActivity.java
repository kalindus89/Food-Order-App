package com.newsapp.foodorderapp.food_cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;

import java.text.DecimalFormat;

public class FoodCartActivity extends AppCompatActivity {

    ImageView goBack;
    TextView totalAmountTxt;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;

    FoodCartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack = findViewById(R.id.go_back);
        totalAmountTxt = findViewById(R.id.totalAmountTxt);
        recyclerView = findViewById(R.id.recyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getTotalPayableAmount();

        syncDataFromFirebase();


    }

    private void getTotalPayableAmount() {

        DocumentReference documentReference = firebaseFirestore.
                collection("FoodOrders").document(new SessionManagement().getPhone(this));

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists()) {
                    DecimalFormat formatter = new DecimalFormat("#,###");

                    int iTotal=Integer.parseInt(String.valueOf(snapshot.getData().get("totalAmount")).replaceAll("[\\D]",""));
                    String total=formatter.format(iTotal);
                    totalAmountTxt.setText(total);
                } else {
                }
            }
        });
    }

    public void syncDataFromFirebase() {
        Query query = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getPhone(this)).collection("orderFoods").orderBy("orderTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CartModel> allUserNotes = new FirestoreRecyclerOptions.Builder<CartModel>().setQuery(query, CartModel.class).build();

        cartAdapter = new FoodCartAdapter(this, allUserNotes, firebaseFirestore);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        cartAdapter.startListening();
        recyclerView.setAdapter(cartAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (cartAdapter != null) {
            cartAdapter.stopListening();
            //noteAdapter.startListening();
        }
    }
}