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
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.FavoritosRep;
import com.sergio.facebookteste.Repository.UsersLocalRep;

import java.util.List;

public class FavoritosList extends AppCompatActivity {

    RecyclerView rv;
    DatabaseHelper db;
    FavoritosRep favoritosRep;
    Session ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        favoritosRep = new FavoritosRep(db.openConnection());
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        loadFavoritos();
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

    private void logout(){
        ss.setLoggedin(false, null);
        ss.setLoggedinFacebook(false, null);
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(FavoritosList.this, LoginActivity.class));
    }

    private void loadFavoritos() {
        Login login;
        UsersLocalRep usersLocalRep = new UsersLocalRep(db.openConnection());
        login = usersLocalRep.getLocalUser(ss.getLogin());
        List<Escola> idEscolas = favoritosRep.getAllUserFavoritos(login.getId());
    }
}

