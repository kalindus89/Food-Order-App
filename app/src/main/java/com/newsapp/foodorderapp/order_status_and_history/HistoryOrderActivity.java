package com.newsapp.foodorderapp.order_status_and_history;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.food_cart_place_order.OrderPlacedModel;

public class HistoryOrderActivity extends AppCompatActivity {


    ImageView goBack;
    RecyclerView recyclerView;
    AdapterOrderStatus adapterOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack = findViewById(R.id.go_back);
        recyclerView = findViewById(R.id.recyclerView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseFirestore.getInstance().collection("FoodOrders").document(new SessionManagement().getPhone(this)).collection("orderFoods").document("00000orderHistory").collection("ongoingOrderIds").document("0000allOrders").collection("placedOrderIds");
        FirestoreRecyclerOptions<OrderPlacedModel> allUserNote= new FirestoreRecyclerOptions.Builder<OrderPlacedModel>().setQuery(query, OrderPlacedModel.class).build();

        adapterOrderStatus  = new AdapterOrderStatus(allUserNote,this,true);

        recyclerView.setAdapter(adapterOrderStatus);
        adapterOrderStatus.notifyDataSetChanged();

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapterOrderStatus.startListening();
        recyclerView.setAdapter(adapterOrderStatus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterOrderStatus.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}