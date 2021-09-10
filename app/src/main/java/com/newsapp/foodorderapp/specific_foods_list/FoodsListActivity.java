package com.newsapp.foodorderapp.specific_foods_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.all_foods_home.BannerModel;
import com.newsapp.foodorderapp.food_cart_place_order.FoodCartActivity;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FoodsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView goBack;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FoodsCategoryAdapter foodAdapter;
    SliderLayout slider_promotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_list);

        recyclerView=findViewById(R.id.recyclerView);
        slider_promotions = findViewById(R.id.slider_promotions);
        goBack=findViewById(R.id.goBack);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setUpSlider();

        String catId=getIntent().getStringExtra("cat_id");

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Foods");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
       // recyclerView.setHasFixedSize(true); nested ScrollerView not work

        Query query=databaseReference.orderByChild("menuID").equalTo(catId);

        FirebaseRecyclerOptions<FoodsModel> allUserNotes = new FirebaseRecyclerOptions.Builder<FoodsModel>().setQuery(query, FoodsModel.class).build();
        foodAdapter  = new FoodsCategoryAdapter(allUserNotes,this);

        recyclerView.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
        recyclerView.setAdapter(foodAdapter);
    }

    public void setUpSlider() {
        HashMap<String, String> imageList  = new HashMap<>();
        DatabaseReference sliderReference = FirebaseDatabase.getInstance().getReference("Banners");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    BannerModel bannerModel = dataSnapshot.getValue(BannerModel.class);
                    imageList.put(bannerModel.getName() + "@@@" + bannerModel.getFoodId(), bannerModel.getImage());
                }

                for (String key : imageList.keySet()) {

                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    final TextSliderView textSliderView = new TextSliderView(getApplicationContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(imageList.get(key))
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(getApplicationContext(), FoodDetailActivity.class);
                                    intent.putExtra("food_id", idOfFood);
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("food_id", idOfFood);

                    textSliderView.setPicasso(Picasso.get());

                    slider_promotions.addSlider(textSliderView);
                    sliderReference.removeEventListener(this); // help only listen one time from FirebaseDatabase

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        sliderReference.addValueEventListener(valueEventListener);


        slider_promotions.setPresetTransformer(SliderLayout.Transformer.Default); // change animation
        slider_promotions.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider_promotions.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.promo_preview));
        slider_promotions.setCustomAnimation(new DescriptionAnimation());
        slider_promotions.setDuration(4000);
    }
    @Override
    protected void onStop() {
        super.onStop();
        slider_promotions.startAutoCycle();
    }
}