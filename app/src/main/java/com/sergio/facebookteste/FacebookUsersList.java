package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.UsersFacebookRep;

import java.util.List;

public class FacebookUsersList extends AppCompatActivity {

    RecyclerView rv;
    DatabaseHelper db;
    UsersFacebookRep usersFacebookRep;
    Session ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        usersFacebookRep = new UsersFacebookRep(db.openConnection());
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));
        loadFacebookUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logout();
                break;
        }
        return false;
    }

    private void redirectToEscolasAdminConfig() {
        startActivity(new Intent(FacebookUsersList.this, EscolaAdd.class));
    }

    private void logout(){
        ss.setLoggedin(false, null);
        ss.setLoggedinFacebook(false, null);
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(FacebookUsersList.this, LoginActivity.class));
    }

    private void loadFacebookUsers() {
        List<Login> allUsers = usersFacebookRep.getAllFacebookUsers();
        FacebookAdapter adapter = new FacebookAdapter(allUsers, this);
        rv.setAdapter(adapter);
    }
}

