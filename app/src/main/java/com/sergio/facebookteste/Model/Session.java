package com.sergio.facebookteste.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor facebook;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = prefs.edit();
        facebook = prefs.edit();
    }

    public void setLoggedin(boolean logggedin, String login){
        editor.putBoolean("loggedInmode",logggedin);
        editor.putString("login",login);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }

    public String getLogin(){
        return prefs.getString("login",null);
    }

    //FACEBOOK
    public void setLoggedinFacebook(boolean loggedin, String login){
        facebook.putBoolean("loggedInmodeFace",loggedin);
        facebook.putString("loginFace",login);
        facebook.commit();
    }

    public boolean loggedinFacebook(){
        return prefs.getBoolean("loggedInmodeFace", false);
    }
}