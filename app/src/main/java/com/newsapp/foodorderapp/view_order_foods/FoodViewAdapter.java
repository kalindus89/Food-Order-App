package com.newsapp.foodorderapp.view_order_foods;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.food_cart_place_order.CartModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class FoodViewAdapter extends FirestoreRecyclerAdapter<CartModel, FoodViewAdapter.CartViewHolder> {


    Context context;
    FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions;
    //Map<String, Object> ordersList;
    public FoodViewAdapter(Context context, @NonNull FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions) {
        super(fireStoreRecyclerOptions);
        this.context = context;
        this.fireStoreRecyclerOptions = fireStoreRecyclerOptions;
    }
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartModel model) {


       // System.out.println("aaaaaa "+ ordersList.get(position).getOrderID()+" "+fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());

      //  Toast.makeText(context,fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId(),Toast.LENGTH_SHORT);

        holder.cart_item_name.setText(model.getProductName());

        holder.cart_item_quantity.setText(model.getQuantity());

        holder.cart_item_price.setText("Total: $"+model.getPrice()+" x "+(model.getQuantity())+" = $"+model.getItemTotal());

        String date_time =(new SimpleDateFormat("EEEE MMM d - hh.mm aa").format(model.getOrderTime()));
        holder.cart_item_date.setText(date_time);

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foods_only,parent,false);
        return new CartViewHolder(view);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
       private TextView cart_item_name,cart_item_price,cart_item_date,cart_item_quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_item_name =itemView.findViewById(R.id.cart_item_name);
            cart_item_price = itemView.findViewById(R.id.cart_item_price);
            cart_item_date = itemView.findViewById(R.id.cart_item_date);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
        }
    }


}
