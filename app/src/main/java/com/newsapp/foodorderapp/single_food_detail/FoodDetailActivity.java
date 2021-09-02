package com.newsapp.foodorderapp.single_food_detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
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
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity implements RatingDialogListener {


    TextView foodName, foodPrice, food_description, overallAppRating;
    ImageView img_food;
    ProgressBar progressBar;
    RatingBar ratingBar;
    int foodTotalRatings=0,foodTotalVoters=0, currentUserRating=0;
    // Collapsing and expanding layouts or images
    CollapsingToolbarLayout collapsingToolbar;
    FloatingActionButton btnCart;
    ElegantNumberButton number_button;

    String foodId, productID;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;

    CallbackManager callbackManager;
    ShareButton shareButton;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);


        BitmapDrawable bitmapDrawable = (BitmapDrawable)img_food.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();

        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap).build();

        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder() .addPhoto(sharePhoto).build();

        shareButton.setShareContent(sharePhotoContent);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        foodId = getIntent().getStringExtra("food_id");
        callbackManager=CallbackManager.Factory.create();

        databaseReference = FirebaseDatabase.getInstance().getReference("Foods").child(foodId);
        firebaseFirestore = FirebaseFirestore.getInstance();

        shareButton = findViewById(R.id.shareButton);
        number_button = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        overallAppRating = findViewById(R.id.overallAppRating);
        ratingBar = findViewById(R.id.ratingBar);

        food_description = findViewById(R.id.food_description);
        foodPrice = findViewById(R.id.foodPrice);
        foodName = findViewById(R.id.foodName);
        img_food = findViewById(R.id.img_food);
        progressBar = findViewById(R.id.progressBar);

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        displayFoodDetails();
        displayOverallRating(foodId);
        displayUserRating(foodId);

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // TODO perform your action here
                    showRatingDialog(foodId);

                }
                return true;
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                btnCart.setEnabled(false);

                DocumentReference nycRef = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getPhone(getApplicationContext()));

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


    private void displayUserRating(String foodId) {


        FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(this)).child("foodRating").child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    FoodsModel foodsModel = snapshot.getValue(FoodsModel.class);

                    ratingBar.setRating(foodsModel.getRating());
                    currentUserRating=foodsModel.getRating();
                } else {
                    ratingBar.setRating(0);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayOverallRating(String foodId) {

        FirebaseDatabase.getInstance().getReference().child("Foods").child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FoodsModel foodsModel = snapshot.getValue(FoodsModel.class);
                foodTotalRatings=foodsModel.getRating();
                foodTotalVoters=foodsModel.getTotalVoters();
                double totalRating = (Double.valueOf(foodTotalRatings) / Double.valueOf(foodTotalVoters) );

                overallAppRating.setText(String.format("%.1f", totalRating) + "/5");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateCart(boolean newUser) {

        WriteBatch batch = firebaseFirestore.batch();

        int iPrice = Integer.parseInt(foodPrice.getText().toString().replaceAll("[\\D]", ""));
        int iQuantity = Integer.parseInt(number_button.getNumber().replaceAll("[\\D]", ""));
        int itemTotalAmount = iPrice * iQuantity;

        DocumentReference nycRef1 = firebaseFirestore.collection("FoodOrders").document(new SessionManagement().getPhone(getApplicationContext())).collection("orderFoods").document();

        Map<String, Object> note2 = new HashMap<>();
        note2.put("orderTime", new Date());
        note2.put("price", foodPrice.getText().toString());
        note2.put("quantity", number_button.getNumber());
        note2.put("itemTotal", String.valueOf(itemTotalAmount));
        note2.put("productID", productID);
        note2.put("productName", foodName.getText().toString());
        note2.put("status", "draft");
        note2.put("orderID", nycRef1.getId());
        batch.set(nycRef1, note2);

        DocumentReference sfRef2 = firebaseFirestore.document("FoodOrders/" + new SessionManagement().getPhone(getApplicationContext()));

        if (newUser == true) {
            Map<String, Object> note3 = new HashMap<>();
            note3.put("numberOfOrders", 1);
            note3.put("totalAmount", itemTotalAmount);
            batch.set(sfRef2, note3);

        } else {
            batch.update(sfRef2, "numberOfOrders", FieldValue.increment(1));
            batch.update(sfRef2, "totalAmount", FieldValue.increment(itemTotalAmount));
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
                productID = snapshot.getKey();
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

    private void showRatingDialog(String foodId) {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(0)
                .setTitle("Rate this food")
                .setDescription("Please rate your food & give some feedback!")
                .setTitleTextColor(R.color.purple_500)
                .setDescriptionTextColor(R.color.purple_500)
                .setHint("Please write your comment here..")
                .setHintTextColor(R.color.purple_700)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.black)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(this).show();

        // check implement override methods to see button actions

    }

    @Override
    public void onPositiveButtonClicked(int i, @NonNull String s) {

        if(Math.round(ratingBar.getRating())==0){
            Map note = new HashMap();
            note.put("rating",i);
            note.put("feedBack",s);
            FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(this)).child("foodRating").child(foodId).setValue(note);

            Map note2 = new HashMap();
            note2.put("rating",(foodTotalRatings+i));
            note2.put("totalVoters",(foodTotalVoters+1));
            FirebaseDatabase.getInstance().getReference().child("Foods").child(foodId).updateChildren(note2);

            ratingBar.setRating(i);
        }else{
            Map note = new HashMap();
            note.put("rating",i);
            note.put("feedBack",s);
            FirebaseDatabase.getInstance().getReference().child("foodPreferences").child(new SessionManagement().getPhone(this)).child("foodRating").child(foodId).updateChildren(note);

            int newTotalRating=((foodTotalRatings-currentUserRating)+i);
            Map note2 = new HashMap();
            note2.put("rating",newTotalRating);
            FirebaseDatabase.getInstance().getReference().child("Foods").child(foodId).updateChildren(note2);
            ratingBar.setRating(i);
        }

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}