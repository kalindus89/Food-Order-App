package com.newsapp.foodorderapp.specific_foods_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.food_cart_place_order.FoodCartActivity;

public class FoodsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView goBack;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FoodsCategoryAdapter foodAdapter;
    FloatingActionButton viewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_list);

        recyclerView=findViewById(R.id.recyclerView);
        goBack=findViewById(R.id.goBack);
        viewCart=findViewById(R.id.viewCart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(FoodsListActivity.this, FoodCartActivity.class));
            }
        });

        String catId=getIntent().getStringExtra("cat_id");

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Foods");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query=databaseReference.orderByChild("menuID").equalTo(catId);

        FirebaseRecyclerOptions<FoodsModel> allUserNotes = new FirebaseRecyclerOptions.Builder<FoodsModel>().setQuery(query, FoodsModel.class).build();
        foodAdapter  = new FoodsCategoryAdapter(allUserNotes,this);

        recyclerView.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
        recyclerView.setAdapter(foodAdapter);
    }
}