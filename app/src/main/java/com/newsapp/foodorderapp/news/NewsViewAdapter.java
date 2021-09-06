package com.newsapp.foodorderapp.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.newsapp.foodorderapp.R;


public class NewsViewAdapter extends FirebaseRecyclerAdapter<NewsModel, NewsViewAdapter.NewsViewHolder> {


    Context context;
    FirebaseRecyclerOptions<NewsModel> options;

    public NewsViewAdapter(@NonNull FirebaseRecyclerOptions<NewsModel> options,Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }
    @Override
    protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull NewsModel model) {

        holder.news_title.setText(model.getTitle());
        holder.news_message.setText(model.getMessage());

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news,parent,false);
        return new NewsViewHolder(view);
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder {
       private TextView news_title,news_message;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            news_title =itemView.findViewById(R.id.news_title);
            news_message = itemView.findViewById(R.id.news_message);
        }
    }


}
