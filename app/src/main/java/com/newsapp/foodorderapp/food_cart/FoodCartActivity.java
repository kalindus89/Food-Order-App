package com.newsapp.foodorderapp.food_cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

public class FoodCartActivity extends AppCompatActivity {

    ImageView goBack;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;

    FoodCartAdapter cartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack=findViewById(R.id.go_back);
        recyclerView=findViewById(R.id.recyclerView);

        firebaseFirestore=FirebaseFirestore.getInstance();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        syncDataFromFirebase();

    }

    public void syncDataFromFirebase(){
        Query query = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getPhone(this)).collection("orderFoods").orderBy("orderTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<CartModel> allUserNotes = new FirestoreRecyclerOptions.Builder<CartModel>().setQuery(query, CartModel.class).build();

        cartAdapter = new FoodCartAdapter(this,allUserNotes,firebaseFirestore);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
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

        if(cartAdapter!=null)
        {
            cartAdapter.stopListening();
            //noteAdapter.startListening();
        }
    }
}