package com.newsapp.foodorderapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {

    public  void setUserName(Context con, String number){
        SharedPreferences.Editor editor = con.getSharedPreferences("username", MODE_PRIVATE).edit();
        editor.putString("phone", number);
        editor.apply();
    }

    public String getUsername(Context con){
        SharedPreferences prefs = con.getSharedPreferences("username", MODE_PRIVATE);
        return prefs.getString("phone", "No name defined");
    }
}
