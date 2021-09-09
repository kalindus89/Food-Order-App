package com.newsapp.foodorderapp.all_foods_home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

public class AdapterQuickFoods extends FirebaseRecyclerAdapter<FoodsModel, AdapterQuickFoods.FoodViewHolder> {

    Context context;
    FirebaseRecyclerOptions<FoodsModel> options;


    public AdapterQuickFoods(@NonNull FirebaseRecyclerOptions<FoodsModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterQuickFoods.FoodViewHolder holder, int position, @NonNull FoodsModel model) {

        holder.foodName.setText(model.getName());
        holder.item_price.setText("$"+model.getPrice());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.loading_image).into(holder.foodImage);
        double totalRating=(Double.valueOf(model.getRating())/ Double.valueOf(model.getTotalVoters()));
       // holder.rating_food.setText("Rating: "+String.format("%.1f", totalRating)+"/5");

        holder.ratingBar.setRating(Float.valueOf(String.format("%.1f", totalRating)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food_id", getRef(holder.getAbsoluteAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });

    }

    @NonNull
    @Override
    public AdapterQuickFoods.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_quick_list, parent, false);
        return new FoodViewHolder(view);
    }


    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName,item_price;
        private ImageView foodImage;
        RatingBar ratingBar;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            item_price = itemView.findViewById(R.id.item_price);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


}
