package com.newsapp.foodorderapp.food_cart_place_order;

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

import java.text.SimpleDateFormat;
import java.util.List;

public class FoodCartAdapter extends FirestoreRecyclerAdapter<CartModel,FoodCartAdapter.CartViewHolder> {


    Context context;
    FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions;
    FirebaseFirestore firebaseFirestore;
    //Map<String, Object> ordersList;
    List<CartModel> ordersList ;
    public FoodCartAdapter(Context context, @NonNull FirestoreRecyclerOptions<CartModel> fireStoreRecyclerOptions, FirebaseFirestore firebaseFirestore,List<CartModel> ordersList) {
        super(fireStoreRecyclerOptions);
        this.context = context;
        this.fireStoreRecyclerOptions = fireStoreRecyclerOptions;
        this.firebaseFirestore = firebaseFirestore;
        this.ordersList = ordersList;

    }
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartModel model) {


        ordersList.add(model);
       // System.out.println("aaaaaa "+ ordersList.get(position).getOrderID()+" "+fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());

      //  Toast.makeText(context,fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId(),Toast.LENGTH_SHORT);

        holder.cart_item_name.setText(model.getProductName());

        holder.cart_item_quantity.setText(model.getQuantity());

        holder.cart_item_price.setText("Total: $"+model.getPrice()+" x "+(model.getQuantity())+" = $"+model.getItemTotal());

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

                        //int itemIndex=ordersList.indexOf(fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());
                        ordersList.remove(holder.getAbsoluteAdapterPosition());

                     DocumentReference documentReference = firebaseFirestore.
                             collection("FoodOrders").document(new SessionManagement().getPhone(context)).collection("orderFoods").
                             document(fireStoreRecyclerOptions.getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition()).getId());

                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                popupMenu.dismiss();

                                //ordersList.remove(itemIndex);
                                WriteBatch batch = firebaseFirestore.batch();
                                DocumentReference sfRef2 = firebaseFirestore.document("FoodOrders/"+new SessionManagement().getPhone(context));

                                batch.update(sfRef2, "numberOfOrders", FieldValue.increment(-1));
                                batch.update(sfRef2, "totalAmount", FieldValue.increment(-(Integer.parseInt(model.getItemTotal().replaceAll("[\\D]","")))));

                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, model.getProductName()+" Item Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });

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
