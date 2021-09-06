package com.newsapp.foodorderapp.all_foods_home;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.FacebookSdk;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.newsapp.foodorderapp.R;
import com.newsapp.foodorderapp.SessionManagement;
import com.newsapp.foodorderapp.WelcomeActivity;
import com.newsapp.foodorderapp.food_cart_place_order.FoodCartActivity;
import com.newsapp.foodorderapp.order_status_and_history.HistoryOrderActivity;
import com.newsapp.foodorderapp.order_status_and_history.OrderStatusActivity;
import com.newsapp.foodorderapp.single_food_detail.FoodDetailActivity;
import com.newsapp.foodorderapp.specific_foods_list.FoodsModel;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView drawerIcon, searchIcon, backToMainPage;
    TextView textView;
    RecyclerView recyclerView;
    EditText searchKeyword;
    SwipeRefreshLayout swipeRefreshList;

    LinearLayout searchLayout, ll_First, ll_Second, current_status, ll_Third, ll_Fourth, ll_Fifth, ll_Sixth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    AdapterCategory catAdapter;
    // FloatingActionButton viewCart;
    CounterFab viewCart;

    FirebaseRecyclerOptions<CategoryModel> allUserNotes;
    ArrayList<CategoryModel> categoryModelArrayList;

    //slider
    HashMap<String, String> imageList;
    SliderLayout slider_promotions;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // FacebookSdk.setApplicationId("157162859896293");
        FacebookSdk.sdkInitialize(this);

        recyclerView = findViewById(R.id.recyclerView);
        viewCart = findViewById(R.id.viewCart);
        searchIcon = findViewById(R.id.searchIcon);
        textView = findViewById(R.id.textView);
        searchLayout = findViewById(R.id.searchLayout);
        backToMainPage = findViewById(R.id.backToMainPage);
        searchKeyword = findViewById(R.id.searchKeyword);
        swipeRefreshList = findViewById(R.id.swipeRefreshList);
        slider_promotions = findViewById(R.id.slider_promotions);

        imageList = new HashMap<>();

        onSetNavigationDrawerEvents();

        categoryModelArrayList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Category");

        // viewCart.setCount(10);

        swipeRefreshList.setColorSchemeColors(R.color.purple_500, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeRefreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (SessionManagement.isConnectedToInternet(getApplicationContext())) {
                    loadDataRefresh();
                    updateFirebaseToken();
                } else {
                    Toast.makeText(getApplicationContext(), "Check your connectivity!", Toast.LENGTH_SHORT).show();
                    swipeRefreshList.setRefreshing(false);
                }
            }
        });

        // load data first time
        swipeRefreshList.post(new Runnable() {
            @Override
            public void run() {
            }
        });


        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, FoodCartActivity.class));
            }
        });

        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIcon.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                searchKeyword.setText("");
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIcon.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);

            }
        });

        searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString() != null) {
                    searchData(editable.toString());

                } else {
                    searchData("");
                }

            }
        });

        // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        setUpSlider();
        loadData();
        getTotalFoodItemCount();
        updateFirebaseToken();


    }

    public void setUpSlider() {

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
        slider_promotions.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.loading_image));
        slider_promotions.setCustomAnimation(new DescriptionAnimation());
        slider_promotions.setDuration(4000);
    }

    public void getTotalFoodItemCount() {

        FirebaseFirestore.getInstance().document("FoodOrders/" + new SessionManagement().getPhone(getApplicationContext())).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    viewCart.setCount(Integer.parseInt(String.valueOf(value.get("numberOfOrders"))));
                }
            }
        });


    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                System.out.println("aaaaa " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }


    public void updateFirebaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "token receive failed", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        String refreshToken = task.getResult();

                        if (refreshToken.equals(new SessionManagement().getFBToken(getApplicationContext()))) {

                            DocumentReference nycRef = FirebaseFirestore.getInstance().document("FoodOrders/" + new SessionManagement().getPhone(getApplicationContext()));
                            nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Map<String, Object> note = new HashMap<>();
                                        note.put("messagingToken", refreshToken);

                                        nycRef.update(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                new SessionManagement().setFBToken(getApplicationContext(), refreshToken);
                                            }
                                        });

                                    } else {
                                        Map<String, Object> note = new HashMap<>();
                                        note.put("totalAmount", 0);
                                        note.put("numberOfOrders", 0);
                                        note.put("messagingToken", refreshToken);
                                        nycRef.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                new SessionManagement().setFBToken(getApplicationContext(), refreshToken);
                                            }
                                        });

                                    }

                                }
                            });

                        }
                    }
                });
    }


    private void loadData() {


        Query query = databaseReference;

        allUserNotes = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();
        catAdapter = new AdapterCategory(allUserNotes, this);


        recyclerView.setAdapter(catAdapter);
        catAdapter.notifyDataSetChanged();

    }

    private void loadDataRefresh() {

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        catAdapter.startListening();
        swipeRefreshList.setRefreshing(false);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    private void searchData(String keyword) {

        Query query = databaseReference.orderByChild("name").startAt(keyword).endAt(keyword + "\uf8ff");

        FirebaseRecyclerOptions<CategoryModel> allUserNotes2 = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(query, CategoryModel.class).build();


        catAdapter = new AdapterCategory(allUserNotes2, this);

        recyclerView.setAdapter(catAdapter);
        catAdapter.startListening();
        catAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        catAdapter.startListening();
        recyclerView.setAdapter(catAdapter);
        getTotalFoodItemCount();
    }

    private void onSetNavigationDrawerEvents() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        //drawerLayout.openDrawer(GravityCompat.END);

        drawerIcon = (ImageView) findViewById(R.id.drawerIcon);
        TextView userName = findViewById(R.id.userName);
        userName.setText(new SessionManagement().getName(this));
        ll_First = (LinearLayout) findViewById(R.id.ll_First);
        ll_Second = (LinearLayout) findViewById(R.id.ll_Second);
        current_status = (LinearLayout) findViewById(R.id.current_status);
        ll_Third = (LinearLayout) findViewById(R.id.ll_Third);
        ll_Fourth = (LinearLayout) findViewById(R.id.ll_Fourth);
        ll_Fifth = (LinearLayout) findViewById(R.id.ll_Fifth);
        ll_Sixth = (LinearLayout) findViewById(R.id.ll_Sixth);

        drawerIcon.setOnClickListener(this);
        ll_First.setOnClickListener(this);
        ll_Second.setOnClickListener(this);
        current_status.setOnClickListener(this);
        ll_Third.setOnClickListener(this);
        ll_Fourth.setOnClickListener(this);
        ll_Fifth.setOnClickListener(this);
        ll_Sixth.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawerIcon:
                drawerLayout.openDrawer(navigationView, true);
                break;
            case R.id.ll_First:
                showToast("ll_First");
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Second:

                startActivity(new Intent(HomeActivity.this, FoodCartActivity.class));
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.current_status:
                Intent intent2 = new Intent(HomeActivity.this, OrderStatusActivity.class);
                startActivity(intent2);
                drawerLayout.closeDrawer(navigationView, true);

                break;
            case R.id.ll_Third:
                showToast("ll_Third");
                Intent intent3 = new Intent(HomeActivity.this, HistoryOrderActivity.class);
                startActivity(intent3);
                drawerLayout.closeDrawer(navigationView, true);
                break;
            case R.id.ll_Fourth:
                showToast("subscribe");
                drawerLayout.closeDrawer(navigationView, true);

                FirebaseMessaging.getInstance().subscribeToTopic("news");

                /*Map note = new HashMap();
                note.put("number", FirebaseDatabase.getInstance().in);
                FirebaseDatabase.getInstance().getReference().child("test").updateChildren(note);*/


                /*DocumentReference nycRef = FirebaseFirestore.getInstance().collection("FoodOrders").document(new SessionManagement().getPhone(getApplicationContext()));

                nycRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_SHORT).show();

                                Map<String, Object> note = new HashMap<>();
                                note.put("favorite", FieldValue.arrayUnion("rrr"));
                               // note.put("favorite", FieldValue.arrayUnion("rrr"));

                                nycRef.update(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                    }
                                });

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Not ok big", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
                break;
            case R.id.ll_Fifth:
                showToast("unsubscribe");
                drawerLayout.closeDrawer(navigationView, true);

                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                break;
            case R.id.ll_Sixth:
                showToast("tv_logout");
                drawerLayout.closeDrawer(navigationView, true);

                new SessionManagement().setUserName(this, "no number", "no name", "log out");

                Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                showToast("Default");
                drawerLayout.closeDrawer(navigationView, true);
                break;

        }


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        slider_promotions.startAutoCycle();
    }
}