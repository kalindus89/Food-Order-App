package com.newsapp.foodorderapp.favorites_foods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.specific_foods_list.FoodsCategoryAdapter;
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;

public class FavoritesFoodsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView goBack;
    DatabaseReference databaseReference;
    FavoritesFoodsAdapter favoritesFoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_foods);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView=findViewById(R.id.recyclerView);
        goBack=findViewById(R.id.goBack);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        databaseReference=FirebaseDatabase.getInstance().getReference("foodPreferences").child(new SessionManagement().getPhone(this));

        Query query=databaseReference;
        /*ClassSnapshotParser parser = new ClassSnapshotParser<FoodsModel>(FoodsModel.class);
        FilterableFirebaseArray filterableFirebaseArray = new FilterableFirebaseArray(query, parser);

        filterableFirebaseArray.addExclude("foodRating");*/


        FirebaseRecyclerOptions<FoodsModel> allUserNotes = new FirebaseRecyclerOptions.Builder<FoodsModel>().setQuery(query, FoodsModel.class).build();

        favoritesFoodsAdapter  = new FavoritesFoodsAdapter(allUserNotes,this);

        recyclerView.setAdapter(favoritesFoodsAdapter);
        favoritesFoodsAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        favoritesFoodsAdapter.startListening();
        recyclerView.setAdapter(favoritesFoodsAdapter);
    }
}