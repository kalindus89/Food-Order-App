package com.newsapp.foodorderapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.material.snackbar.Snackbar;
import com.newsapp.foodorderapp.signin_signup.SignUpActivity;

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

    public void setState(Context con, String save) {
        SharedPreferences.Editor editor = con.getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("state", save);
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
    }    public boolean getState(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);

        if (prefs.getString("state", "no").equals("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            if (netInfo != null) {

                for (int i = 0; i < netInfo.length; i++) {

                    if (netInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;

                  /*  if (SessionManagement.isConnectedToInternet(context)) {

                      } else {
                         Snackbar.make(view, "Check your connectivity!", Snackbar.LENGTH_SHORT).show();
                         Toast.makeText(this, "Check your connectivity!", Toast.LENGTH_SHORT).show();

                      }
                    */
                }
            }
        }
        return false;
    }


}
