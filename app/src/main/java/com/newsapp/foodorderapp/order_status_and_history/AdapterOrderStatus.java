package com.newsapp.foodorderapp.order_status_and_history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.food_cart_place_order.OrderPlacedModel;
import com.newsapp.foodorderapp.view_order_foods.ViewOrderFoods;
import com.squareup.picasso.Picasso;

public class AdapterOrderStatus extends FirestoreRecyclerAdapter<OrderPlacedModel, AdapterOrderStatus.OderStatusViewHolder> {

    Context context;
    FirestoreRecyclerOptions<OrderPlacedModel> fireStoreRecyclerOptions;
    boolean hideTrack;

    public AdapterOrderStatus(@NonNull FirestoreRecyclerOptions<OrderPlacedModel> fireStoreRecyclerOptions, Context context,boolean hideTrack) {
        super(fireStoreRecyclerOptions);
        this.context = context;
        this.fireStoreRecyclerOptions = fireStoreRecyclerOptions;
        this.hideTrack = hideTrack;
    }

    @Override
    protected void onBindViewHolder(@NonNull OderStatusViewHolder holder, int position, @NonNull OrderPlacedModel model) {


        holder.order_id.setText("Oder ID: "+fireStoreRecyclerOptions.getSnapshots().getSnapshot(position).getId());

        if(model.getStatus().equals("1")){
            holder.order_status.setText("Status: Processing");
            holder.order_status.setTextColor(Color.parseColor("#54BF2D"));
        }else if(model.getStatus().equals("2")){
            holder.order_status.setText("Status: Shipping");
            holder.order_status.setTextColor(Color.parseColor("#F32183"));
        }else if(model.getStatus().equals("3")){
            holder.order_status.setText("Status: Completed");
            holder.order_status.setTextColor(Color.parseColor("#F32183"));
        }else{
            holder.order_status.setText("Status: Placed");
            holder.order_status.setTextColor(Color.parseColor("#2196F3"));
        }

        holder.order_total.setText("Total: $"+model.getTotal());
        holder.order_address.setText("Address: "+model.getAddress());

        if(hideTrack==true){
            holder.btnTrack.setVisibility(View.GONE);
            holder.btnView.setVisibility(View.VISIBLE);
        }else{
            holder.btnTrack.setVisibility(View.VISIBLE);
            holder.btnView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewOrderFoods.class);
                intent.putExtra("order_id",fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());
                intent.putExtra("order_status",model.getStatus());
                intent.putExtra("order_total",model.getTotal());
                intent.putExtra("order_address",model.getAddress());
                context.startActivity(intent);

            }
        });
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewOrderFoods.class);
                intent.putExtra("order_id",fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());
                intent.putExtra("order_status",model.getStatus());
                intent.putExtra("order_total",model.getTotal());
                intent.putExtra("order_address",model.getAddress());
                context.startActivity(intent);

            }
        });


    }

    @NonNull
    @Override
    public OderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status,parent,false);
        return new OderStatusViewHolder(view);
    }

    public class OderStatusViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id,order_status,order_total,order_address;
        Button btnTrack,btnView;

        public OderStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id =itemView.findViewById(R.id.order_id);
            order_status =itemView.findViewById(R.id.order_status);
            order_total =itemView.findViewById(R.id.order_total);
            order_address =itemView.findViewById(R.id.order_address);
            btnTrack =itemView.findViewById(R.id.btnTrack);
            btnView =itemView.findViewById(R.id.btnView);
        }
    }
}
