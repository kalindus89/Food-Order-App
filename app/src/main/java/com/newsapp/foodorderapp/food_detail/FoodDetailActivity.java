package com.newsapp.foodorderapp.food_detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {


    TextView foodName, foodPrice, food_description;
    ImageView img_food;
    ProgressBar progressBar;

    // Collapsing and expanding layouts or images
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton btnCart;
    ElegantNumberButton number_button;

    String foodId,productID;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        foodId = getIntent().getStringExtra("food_id");

        databaseReference = FirebaseDatabase.getInstance().getReference("Foods").child(foodId);
        firebaseFirestore=FirebaseFirestore.getInstance();

        number_button = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);

        food_description = findViewById(R.id.food_description);
        foodPrice = findViewById(R.id.foodPrice);
        foodName = findViewById(R.id.foodName);
        img_food = findViewById(R.id.img_food);
        progressBar = findViewById(R.id.progressBar);

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        displayFoodDetails();

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                btnCart.setEnabled(false);

                DocumentReference nycRef = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getUsername(getApplicationContext()));

                nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                updateCart(false);
                            } else {
                                updateCart(true);
                            }
                        } else {

                            progressBar.setVisibility(View.GONE);
                            btnCart.setEnabled(true);

                            Toast.makeText(getApplicationContext(), "Not ok big", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    private void updateCart(boolean newUser) {

        WriteBatch batch = firebaseFirestore.batch();

        Map<String, Object> note2 = new HashMap<>();
        note2.put("orderTime", new Date());
        note2.put("price", foodPrice.getText().toString());
        note2.put("productID", productID);
        note2.put("productName", foodName.getText().toString());
        note2.put("quantity", number_button.getNumber());
        note2.put("status", "draft");
        DocumentReference nycRef1 = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getUsername(getApplicationContext())).collection("orderFoods").document();
        batch.set(nycRef1, note2);

        DocumentReference sfRef2 = firebaseFirestore.document("FoodOrders/"+new SessionManagement().getUsername(getApplicationContext()));

        if(newUser==true) {
            Map<String, Object> note3 = new HashMap<>();
            note3.put("numberOfOrders", 1);
            batch.set(sfRef2, note3);

        }else{
            batch.update(sfRef2, "numberOfOrders", FieldValue.increment(1));
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                btnCart.setEnabled(true);
                    finish();
            }
        });
    }

    private void displayFoodDetails() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FoodsModel foodsModel = snapshot.getValue(FoodsModel.class);
                productID= snapshot.getKey();
                collapsingToolbar.setTitle(foodsModel.getName());
                food_description.setText(foodsModel.getDescription());
                foodPrice.setText(foodsModel.getPrice());
                foodName.setText(foodsModel.getName());
                Picasso.get().load(foodsModel.getImage()).placeholder(R.drawable.vegi_bg).into(img_food);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}