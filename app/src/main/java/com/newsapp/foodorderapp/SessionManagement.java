package com.newsapp.foodorderapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class SessionManagement {

    public void setUserName(Context con, String number, String name, String login) {
        SharedPreferences.Editor editor = con.getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("phone", number);
        editor.putString("name", name);
        editor.putString("login", login);
        editor.apply();
    }
    public void setFBToken(Context con, String fbToken) {
        SharedPreferences.Editor editor = con.getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("fbToken", fbToken);
        editor.apply();
    }

    public String getFBToken(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);
        return prefs.getString("fbToken", "token name defined");
    }

    public String getPhone(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);
        return prefs.getString("phone", "No name defined");
    }

    public String getName(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);
        return prefs.getString("name", "No name defined");
    }

    public boolean isLogin(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);

        if (prefs.getString("login", "No name defined").equals("yes")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isConnectedToInternet (Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm !=null){
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            if(netInfo !=null){

                for (int i=0; i<netInfo.length;i++){

                    if(netInfo[i].getState()==NetworkInfo.State.CONNECTED)
                        return  true;
                }
            }
        }
        return  false;
    }
}
