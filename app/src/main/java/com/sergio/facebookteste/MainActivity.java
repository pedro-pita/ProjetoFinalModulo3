package com.sergio.facebookteste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class MainActivity extends Activity {
    Session ss;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        adicionarRoot();
        verificarFacebookSession();
    }
    private void adicionarRoot(){
        if (usersLocalRep.getAllLocalUsers().size() == 0) {
            usersLocalRep.addLocalUser("Super Utilizador", "root", "root", "1");
        }
    }
    private void verificarFacebookSession() {
        if (ss.loggedinFacebook() && isLoggedIn()) {
            verificarPermissoes();
        }else{
            verificarLocalSession();
        }
    }

    private void verificarLocalSession() {
        if (ss.loggedin()) {
            verificarPermissoes();
        }else {
            redirectToLoginActivity();
        }
    }

    private void verificarPermissoes(){
        try {
            if((usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("1"))){
                redirectToMainProject();
            }else{
                redirectToEscolasList();
            }
        } catch (Exception e) {
            //Significa que é um user do facebook logo não tem permissoes
            finish();
            redirectToEscolasList();
        }
    }
    private void redirectToEscolasList(){
        finish();
        startActivity(new Intent(MainActivity.this, EscolasList.class));
    }
    private boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }
    private void redirectToMainProject() {
        finish();
        startActivity(new Intent(MainActivity.this, MainProject.class));
    }
    private void redirectToLoginActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}