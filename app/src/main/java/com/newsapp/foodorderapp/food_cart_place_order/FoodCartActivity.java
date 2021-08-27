package com.newsapp.foodorderapp.food_cart_place_order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodCartActivity extends AppCompatActivity {

    ImageView goBack;
    TextView totalAmountTxt;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    CardView bottomLayout;
    LinearLayout preview;
    Button btnFoodOrder, btnPlaceOrder;

    List<CartModel> ordersList = new ArrayList<>();

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


    public void placeAnOrder() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodCartActivity.this);
        alertDialog.setTitle("One More Step!");
        alertDialog.setMessage("Enter your address");
        alertDialog.setPositiveButton("Yes", null);
        alertDialog.setNegativeButton("cancel", null);

        final EditText editText = new EditText(FoodCartActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setHint("Your address");
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        final AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        ProgressDialog dialog = ProgressDialog.show(FoodCartActivity.this, "",
                                "Placing Order. Please wait...", true);
                        dialog.show();

                        if (!editText.getText().toString().isEmpty()) {

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PlaceOrders").push();
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    OrderPlacedModel user = new OrderPlacedModel(new SessionManagement().getName(getApplicationContext()),
                                            new SessionManagement().getPhone(getApplicationContext()), editText.getText().toString(),
                                            totalAmountTxt.getText().toString(), ordersList);
                                    databaseReference.setValue(user);

                                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("driverNumber", "0");
                                    note.put("status", "0");
                                    note.put("order_id", snapshot.getRef().getKey());
                                    note.put("total", totalAmountTxt.getText().toString());
                                    note.put("address", editText.getText().toString());

                                    DocumentReference nycRef = FirebaseFirestore.getInstance().document("FoodOrders/"+new SessionManagement().getPhone(getApplicationContext())+"/"+"orderFoods/"+"00000orderHistory/"+"ongoingOrderIds/"+snapshot.getRef().getKey());
                                    batch.set(nycRef, note);

                                    Map<String, Object> note2 = new HashMap<>();
                                    note2.put("driverNumber", "0");
                                    note2.put("status", "0");
                                    note2.put("order_id", snapshot.getRef().getKey());
                                    note2.put("total", totalAmountTxt.getText().toString());
                                    note2.put("address", editText.getText().toString());
                                    DocumentReference nycRef2 = FirebaseFirestore.getInstance().document("FoodOrders/"+new SessionManagement().getPhone(getApplicationContext())+"/"+"orderFoods/"+"00000orderHistory/"+"ongoingOrderIds/"+"0000allOrders/"+"placedOrderIds/"+snapshot.getRef().getKey());
                                    batch.set(nycRef2, note2);


                                    // Commit the batch
                                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            for (int i = 0; i < ordersList.size(); i++) {

                                                Map<String, Object> note3 = new HashMap<>();
                                                note3.put("orderTime", ordersList.get(i).getOrderTime());
                                                note3.put("price", ordersList.get(i).getPrice());
                                                note3.put("quantity", ordersList.get(i).getQuantity());
                                                note3.put("itemTotal", ordersList.get(i).getItemTotal());
                                                note3.put("productID", ordersList.get(i).getProductID());
                                                note3.put("productName", ordersList.get(i).getProductName());
                                                note3.put("status", "draft");

                                                FirebaseFirestore.getInstance().document("FoodOrders/"+new SessionManagement().getPhone(getApplicationContext())+"/"+"orderFoods/"+"00000orderHistory/"+"ongoingOrderIds/"+"0000allOrders/"+"placedOrderIds/"
                                                        +snapshot.getRef().getKey()+"/orderFoods/"+ordersList.get(i).getOrderID()).set(note3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(), "Order Successfully Placed", Toast.LENGTH_SHORT).show();


                                                    }
                                                });

                                            }

                                            WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();


                                            Map<String, Object> note4 = new HashMap<>();
                                            note4.put("numberOfOrders", 0);
                                            note4.put("totalAmount", 0);
                                            DocumentReference sfRef4 = FirebaseFirestore.getInstance().document("FoodOrders/"+new SessionManagement().getPhone(getApplicationContext()));
                                            writeBatch.update(sfRef4,note4);

                                            for (int i=0;i<ordersList.size();i++){
                                                DocumentReference documentReference = FirebaseFirestore.getInstance().document("FoodOrders/"+new SessionManagement().getPhone(getApplicationContext())+"/"+"orderFoods/"+ordersList.get(i).getOrderID());
                                                writeBatch.delete(documentReference);
                                            }

                                            writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Do anything here
                                                    preview.setVisibility(View.VISIBLE);
                                                    recyclerView.setVisibility(View.GONE);
                                                    bottomLayout.setVisibility(View.GONE);
                                                    editText.setText("0.00");
                                                    dialog.dismiss();
                                                    mAlertDialog.dismiss();
                                                }
                                            });

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    mAlertDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed place order", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Please enter your address", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        mAlertDialog.show();
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

                    } else {

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

        cartAdapter = new FoodCartAdapter(this, allUserNotes, firebaseFirestore, ordersList);
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