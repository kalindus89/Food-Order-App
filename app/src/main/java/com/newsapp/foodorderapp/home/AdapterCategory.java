package com.newsapp.foodorderapp.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.newsapp.foodorderapp.R;
import com.squareup.picasso.Picasso;

public class AdapterCategory extends FirebaseRecyclerAdapter<CategoryModel,AdapterCategory.CatViewHolder> {

    Context context;
    FirebaseRecyclerOptions<CategoryModel> options;

    public AdapterCategory(@NonNull FirebaseRecyclerOptions<CategoryModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterCategory.CatViewHolder holder, int position, @NonNull CategoryModel model) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.catName.setText(model.getName());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.vegi_bg).into(holder.cateImage);

    }

    @NonNull
    @Override
    public AdapterCategory.CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
        return new CatViewHolder(view);
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;
        private ImageView cateImage;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            cateImage =itemView.findViewById(R.id.cateImage);
            catName = itemView.findViewById(R.id.catName);
        }
    }
}
