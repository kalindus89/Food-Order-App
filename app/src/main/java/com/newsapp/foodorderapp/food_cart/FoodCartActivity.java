package com.newsapp.foodorderapp.food_cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodCartActivity extends AppCompatActivity {

    ImageView goBack;
    TextView totalAmountTxt;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    CardView bottomLayout;
    LinearLayout preview;
    Button btnFoodOrder,btnPlaceOrder;

  //  Map<String, Object> ordersList = new HashMap<>();;

    List<String> ordersList = new ArrayList<>();

    FoodCartAdapter cartAdapter;
    boolean syncData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack = findViewById(R.id.go_back);
        totalAmountTxt = findViewById(R.id.totalAmountTxt);
        recyclerView = findViewById(R.id.recyclerView);
        preview = findViewById(R.id.preview);
        bottomLayout = findViewById(R.id.bottomLayout);
        btnFoodOrder = findViewById(R.id.btnFoodOrder);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        firebaseFirestore = FirebaseFirestore.getInstance();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFoodOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeAnOrder();
            }
        });

       getTotalPayableAmount();

        syncDataFromFirebase();
        syncData = true;

    }

    private void placeAnOrder() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodCartActivity.this);
        alertDialog.setTitle("One More Step!");
        alertDialog.setMessage("Enter your address");

        final EditText editText = new EditText(FoodCartActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);;
        editText.setHint("Your address");
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PlaceOrders").child(new SessionManagement().getPhone(getApplicationContext())).push();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                       /* Set<String> hashSet = new LinkedHashSet(ordersList);
                        List<String> removedDuplicates = new ArrayList(hashSet);*/

                            OrderPlacedModel user = new OrderPlacedModel(new SessionManagement().getName(getApplicationContext()),
                                    new SessionManagement().getPhone(getApplicationContext()),editText.getText().toString(),
                                    totalAmountTxt.getText().toString(),ordersList);
                            databaseReference.setValue(user);

                            Toast.makeText(getApplicationContext(), "Order Successfully Placed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Failed Sign up", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void getTotalPayableAmount() {

        DocumentReference documentReference = firebaseFirestore.
                collection("FoodOrders").document(new SessionManagement().getPhone(this));

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists()) {

                    if (!String.valueOf(snapshot.getData().get("totalAmount")).equals("0")) {

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        int iTotal = Integer.parseInt(String.valueOf(snapshot.getData().get("totalAmount")).replaceAll("[\\D]", ""));
                        String total = formatter.format(iTotal);
                        totalAmountTxt.setText(total);

                        preview.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);

                    }else{

                        preview.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        bottomLayout.setVisibility(View.GONE);
                    }


                } else {
                    preview.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void syncDataFromFirebase() {
        Query query = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getPhone(this)).collection("orderFoods").orderBy("orderTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CartModel> allUserNotes = new FirestoreRecyclerOptions.Builder<CartModel>().setQuery(query, CartModel.class).build();

        cartAdapter = new FoodCartAdapter(this, allUserNotes, firebaseFirestore,ordersList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (syncData == true) {
            cartAdapter.startListening();
            recyclerView.setAdapter(cartAdapter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (syncData == true) {
            if (cartAdapter != null) {
                cartAdapter.stopListening();
                //noteAdapter.startListening();
            }
        }
    }
}