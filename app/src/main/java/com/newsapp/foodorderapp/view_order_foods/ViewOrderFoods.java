package com.newsapp.foodorderapp.view_order_foods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.food_cart_place_order.CartModel;
import com.newsapp.foodorderapp.food_cart_place_order.FoodCartAdapter;

public class ViewOrderFoods extends AppCompatActivity {

    TextView order_id,order_status,order_total,order_address;
    ImageView goBack;
    FoodViewAdapter cartAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_foods);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        order_id= findViewById(R.id.order_id);
        order_status= findViewById(R.id.order_status);
        order_total= findViewById(R.id.order_total);
        order_address= findViewById(R.id.order_address);
        recyclerView= findViewById(R.id.recyclerView);

        goBack= findViewById(R.id.goBack);

        order_id.setText("Oder ID: "+getIntent().getStringExtra("order_id"));
        order_status.setText(getIntent().getStringExtra("order_status"));
        order_total.setText("Total: $"+getIntent().getStringExtra("order_total"));
        order_address.setText("Address: "+getIntent().getStringExtra("order_address"));

        if(getIntent().getStringExtra("order_status").equals("1")){
            order_status.setText("Status: Processing");
        }else if(getIntent().getStringExtra("order_status").equals("2")){
            order_status.setText("Status: Shipping");
        }else if(getIntent().getStringExtra("order_status").equals("3")){
            order_status.setText("Status: Completed");
        }else{
            order_status.setText("Status: Placed");
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        syncDataFromFirebase(getIntent().getStringExtra("order_id"));
    }

    public void syncDataFromFirebase(String orderId) {
        Query query = FirebaseFirestore.getInstance().collection("FoodOrders").document(new SessionManagement().getPhone(this)).collection("orderFoods")
                .document("00000orderHistory").collection("ongoingOrderIds").document("0000allOrders").collection("placedOrderIds")
                .document(orderId).collection("orderFoods").orderBy("orderTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CartModel> allUserNotes = new FirestoreRecyclerOptions.Builder<CartModel>().setQuery(query, CartModel.class).build();

        cartAdapter = new FoodViewAdapter(this, allUserNotes);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.startListening();

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
        }
    }
}