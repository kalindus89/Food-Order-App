package com.newsapp.foodorderapp.specific_foods_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

public class FoodsCategoryAdapter extends FirebaseRecyclerAdapter<FoodsModel, FoodsCategoryAdapter.FoodViewHolder> {

    Context context;
    FirebaseRecyclerOptions<FoodsModel> options;
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    public FoodsCategoryAdapter(@NonNull FirebaseRecyclerOptions<FoodsModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodsCategoryAdapter.FoodViewHolder holder, int position, @NonNull FoodsModel model) {

        getFavorites(getRef(holder.getAbsoluteAdapterPosition()).getKey(), holder);

        holder.foodName.setText(model.getName());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.loading_image).into(holder.foodImage);
        double totalRating=(Double.valueOf(model.getRating())/ Double.valueOf(model.getTotalVoters()));
        holder.rating_food.setText("Rating: "+String.format("%.1f", totalRating)+"/5");

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

                if(holder.fav_icon.getTag().toString().equals("my Fav")){
                    removeFromFavorites(getRef(holder.getAbsoluteAdapterPosition()).getKey(),holder);
                }else {
                    addToFavorites(getRef(holder.getAbsoluteAdapterPosition()).getKey(),model.getName(),holder);
                }

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
    public FoodsCategoryAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_list, parent, false);
        return new FoodViewHolder(view);
    }

    public void addToFavorites(String foodId, String foodName, FoodViewHolder holder) {

        Map note = new HashMap();
        note.put("foodId", foodId);
        note.put("name", foodName);
        FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(context)).child(foodId).setValue(note);
        holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_on));
        holder.fav_icon.setTag("my Fav");

    }

    public void removeFromFavorites(String foodId, FoodViewHolder holder) {
        FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(context)).child(foodId).removeValue();
        holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_off));
        holder.fav_icon.setTag("Not my Fav");

    }

    public void getFavorites(String foodId, FoodViewHolder holder) {

        FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(context)).child(foodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_on));
                    holder.fav_icon.setTag("my Fav");

                } else {
                    holder.fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_off));
                    holder.fav_icon.setTag("Not my Fav");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName,rating_food;
        private ImageView foodImage, fav_icon,shareFacebook;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            fav_icon = itemView.findViewById(R.id.fav_icon);
            rating_food = itemView.findViewById(R.id.rating_food);
            shareFacebook = itemView.findViewById(R.id.shareFacebook);
        }
    }


}
