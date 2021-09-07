package com.newsapp.foodorderapp.favorites_foods;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FavoritesFoodsAdapter extends FirebaseRecyclerAdapter<FoodsModel, FavoritesFoodsAdapter.FavoritesViewHolder> {

    Context context;
    FirebaseRecyclerOptions<FoodsModel> options;
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    public FavoritesFoodsAdapter(@NonNull FirebaseRecyclerOptions<FoodsModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull FavoritesFoodsAdapter.FavoritesViewHolder holder, int position, @NonNull FoodsModel model) {


        holder.foodName.setText(model.getName());
        holder.item_price.setText("$"+model.getPrice());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.loading_image).into(holder.foodImage);

        holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_on));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food_id", getRef(holder.getAbsoluteAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });
        holder.fav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromFavorites(getRef(holder.getAbsoluteAdapterPosition()).getKey(),holder);

            }
        });
        holder.shareFacebook.setOnClickListener(new View.OnClickListener() {
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
    public FavoritesFoodsAdapter.FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_list, parent, false);
        return new FavoritesViewHolder(view);
    }


    public void removeFromFavorites(String foodId, FavoritesViewHolder holder) {
        FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(context)).child(foodId).removeValue();
        holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_off));
        holder.fav_icon.setTag("Not my Fav");

    }


    public class FavoritesViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName,item_price;
        private ImageView foodImage, fav_icon,shareFacebook;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            fav_icon = itemView.findViewById(R.id.fav_icon);
            shareFacebook = itemView.findViewById(R.id.shareFacebook);
            item_price = itemView.findViewById(R.id.item_price);
        }
    }


}
