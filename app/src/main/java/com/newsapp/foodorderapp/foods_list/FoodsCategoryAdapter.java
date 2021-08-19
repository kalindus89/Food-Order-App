package com.newsapp.foodorderapp.foods_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.newsapp.foodorderapp.R;
import com.squareup.picasso.Picasso;

public class FoodsCategoryAdapter extends FirebaseRecyclerAdapter<FoodsModel, FoodsCategoryAdapter.FoodViewHolder> {

    Context context;
    FirebaseRecyclerOptions<FoodsModel> options;

    public FoodsCategoryAdapter(@NonNull FirebaseRecyclerOptions<FoodsModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodsCategoryAdapter.FoodViewHolder holder, int position, @NonNull FoodsModel model) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
                Toast.makeText(context, "item clicked "+getRef(holder.getAbsoluteAdapterPosition()).getKey(), Toast.LENGTH_SHORT).show();


            }
        });

        holder.foodName.setText(model.getName());
        Picasso.get().load(model.getImage()).placeholder(R.drawable.vegi_bg).into(holder.foodImage);

    }

    @NonNull
    @Override
    public FoodsCategoryAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_lists_item,parent,false);
        return new FoodViewHolder(view);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView foodName;
        private ImageView foodImage;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage =itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
        }
    }
}
