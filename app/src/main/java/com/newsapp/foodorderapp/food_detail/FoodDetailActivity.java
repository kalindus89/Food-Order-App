package com.newsapp.foodorderapp.food_detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity {


    TextView foodName, foodPrice, food_description;
    ImageView img_food;

    // Collapsing and expanding layouts or images
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton btnCart;
    ElegantNumberButton number_button;

    String foodId;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        foodId = getIntent().getStringExtra("food_id");

        databaseReference = FirebaseDatabase.getInstance().getReference("Foods").child(foodId);

        number_button = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);

        food_description = findViewById(R.id.food_description);
        foodPrice = findViewById(R.id.foodPrice);
        foodName = findViewById(R.id.foodName);
        img_food = findViewById(R.id.img_food);

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        displayFoodDetails();

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), new SessionManagement().getUsername(getApplicationContext()), Toast.LENGTH_SHORT).show();


                DocumentReference nycRef = FirebaseFirestore.getInstance().collection("FoodOrders").document(new SessionManagement().getUsername(getApplicationContext()));

                nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                updateUserOrders();
                            } else {
                                createNewUserAndOrder();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Not ok big", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void createNewUserAndOrder() {
    }

    private void updateUserOrders() {
    }

    private void displayFoodDetails() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FoodsModel foodsModel = snapshot.getValue(FoodsModel.class);
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