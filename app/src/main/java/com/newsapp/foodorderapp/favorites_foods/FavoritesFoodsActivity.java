package com.newsapp.foodorderapp.favorites_foods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.all_foods_home.BannerModel;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.newsapp.foodorderapp.specific_foods_list.FoodsCategoryAdapter;
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FavoritesFoodsActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    LinearLayout rootView;
    ImageView goBack;
    DatabaseReference databaseReference;
    FavoritesFoodsAdapter favoritesFoodsAdapter;
    SliderLayout slider_promotions;
    ShimmerFrameLayout shimmerFrameLayout_food_Items;
    LinearLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_foods);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView=findViewById(R.id.recyclerView);
        goBack=findViewById(R.id.goBack);
        rootView=findViewById(R.id.rootView);
        slider_promotions = findViewById(R.id.slider_promotions);
        shimmerFrameLayout_food_Items = findViewById(R.id.shimmerFrameLayout_food_Items);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setUpSlider();
        shimmerEffects();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
       // recyclerView.setHasFixedSize(true); not work with slider

        databaseReference=FirebaseDatabase.getInstance().getReference("foodPreferences").child(new SessionManagement().getPhone(this));

        Query query=databaseReference;


        FirebaseRecyclerOptions<FoodsModel> allUserNotes = new FirebaseRecyclerOptions.Builder<FoodsModel>().setQuery(query, FoodsModel.class).build();

        favoritesFoodsAdapter  = new FavoritesFoodsAdapter(allUserNotes,this,databaseReference,shimmerFrameLayout_food_Items,shimmerLayout);

        recyclerView.setAdapter(favoritesFoodsAdapter);
        favoritesFoodsAdapter.notifyDataSetChanged();

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelper = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        favoritesFoodsAdapter.startListening();
        recyclerView.setAdapter(favoritesFoodsAdapter);
        slider_promotions.startAutoCycle();
    }

    private void shimmerEffects() {

        shimmerFrameLayout_food_Items.setVisibility(View.VISIBLE);
        shimmerFrameLayout_food_Items.startShimmer();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof FavoritesFoodsAdapter.FavoritesViewHolder){
            String name=((FavoritesFoodsAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAbsoluteAdapterPosition()).getName();
            FoodsModel tempFoodModel= ((FavoritesFoodsAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAbsoluteAdapterPosition());
            String foodId= ((FavoritesFoodsAdapter)recyclerView.getAdapter()).options.getSnapshots().getSnapshot(viewHolder.getAbsoluteAdapterPosition()).getKey();

            favoritesFoodsAdapter.removeItem(position);

            Snackbar snackbar =  Snackbar.make(rootView,name+" remove from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoritesFoodsAdapter.restoreItem(tempFoodModel,position,foodId);
                   // Toast.makeText(getApplicationContext(), "keyy "+key, Toast.LENGTH_SHORT).show();

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

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
        if(favoritesFoodsAdapter!=null){
            favoritesFoodsAdapter.stopListening();
        }
        slider_promotions.stopAutoCycle();
    }
}