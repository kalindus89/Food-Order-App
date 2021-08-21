package com.newsapp.foodorderapp.food_cart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;

import java.text.SimpleDateFormat;

public class FoodCartAdapter extends FirestoreRecyclerAdapter<CartModel,FoodCartAdapter.CartViewHolder> {


    Context context;
    FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions;
    FirebaseFirestore firebaseFirestore;

    public FoodCartAdapter(Context context, @NonNull FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions, FirebaseFirestore firebaseFirestore) {
        super(fireStoreRecyclerOptions);
        this.context = context;
        this.fireStoreRecyclerOptions = fireStoreRecyclerOptions;
        this.firebaseFirestore = firebaseFirestore;
    }
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartModel model) {

        holder.cart_item_name.setText(model.getProductName());

        holder.cart_item_quantity.setText(model.getQuantity());

        int iPrice=Integer.parseInt(model.getPrice().replaceAll("[\\D]",""));
        int iQuantity=Integer.parseInt(model.getQuantity().replaceAll("[\\D]",""));

        holder.cart_item_price.setText("Total: $"+model.getPrice()+" x "+(model.getQuantity())+" = $"+String.valueOf(iPrice*iQuantity));

        String date_time =(new SimpleDateFormat("EEEE MMM d - hh.mm aa").format(model.getOrderTime()));
        holder.cart_item_date.setText(date_time);

        holder.cart_item_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenu().add("Cancel").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        popupMenu.dismiss();
                        return false;
                    }
                });
                popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                     DocumentReference documentReference = firebaseFirestore.
                             collection("FoodOrders").document(new SessionManagement().getPhone(context)).collection("orderFoods").
                             document(fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());

                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, model.getProductName()+" Item Deleted", Toast.LENGTH_SHORT).show();
                                popupMenu.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, model.getProductName()+" Item failed Deleted ", Toast.LENGTH_SHORT).show();
                                popupMenu.dismiss();
                            }
                        });

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new CartViewHolder(view);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
       private TextView cart_item_name,cart_item_price,cart_item_date,cart_item_quantity;
       private ImageView cart_item_delete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_item_name =itemView.findViewById(R.id.cart_item_name);
            cart_item_price = itemView.findViewById(R.id.cart_item_price);
            cart_item_date = itemView.findViewById(R.id.cart_item_date);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
            cart_item_delete = itemView.findViewById(R.id.cart_item_delete);
        }
    }
}
