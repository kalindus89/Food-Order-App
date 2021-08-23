package com.newsapp.foodorderapp.order_status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.all_foods_home.AdapterCategory;
import com.newsapp.foodorderapp.all_foods_home.CategoryModel;
import com.newsapp.foodorderapp.food_cart.OrderPlacedModel;

public class OrderStatusActivity extends AppCompatActivity {


    ImageView goBack;
    RecyclerView recyclerView;
    AdapterOrderStatus adapterOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

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

        Query query=FirebaseDatabase.getInstance().getReference("PlaceOrders").child(new SessionManagement().getPhone(this));

        FirebaseRecyclerOptions<OrderPlacedModel> allUserNotes = new FirebaseRecyclerOptions.Builder<OrderPlacedModel>().setQuery(query, OrderPlacedModel.class).build();
        adapterOrderStatus  = new AdapterOrderStatus(allUserNotes,this);

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