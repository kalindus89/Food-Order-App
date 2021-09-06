package com.newsapp.foodorderapp.news;

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
import com.newsapp.foodorderapp.all_foods_home.CategoryModel;

public class NewsActivity extends AppCompatActivity {

    ImageView goBack;
    NewsViewAdapter newsAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack = findViewById(R.id.goBack);
        recyclerView = findViewById(R.id.recyclerView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        syncNewsFromFirebase();

    }

    public void syncNewsFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference("News");

        FirebaseRecyclerOptions<NewsModel>   allNews = new FirebaseRecyclerOptions.Builder<NewsModel>().setQuery(query, NewsModel.class).build();
        newsAdapter = new NewsViewAdapter(allNews, this);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(newsAdapter);
        newsAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        newsAdapter.startListening();
        recyclerView.setAdapter(newsAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (newsAdapter != null) {
            newsAdapter.stopListening();
        }
    }
}