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
import com.sergio.facebookteste.Repository.UsersLocalRep;

import java.util.List;

public class LocalUsersList extends AppCompatActivity {

    RecyclerView rv;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;
    Session ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                redirectToLocalUsersEdit();
                break;
            case R.id.logout:
                logout();
        }
        return false;
    }

    private void redirectToLocalUsersEdit() {
        startActivity(new Intent(LocalUsersList.this, LocalUsersAdd.class));
    }

    private void logout(){
        ss.setLoggedin(false, null);
        ss.setLoggedinFacebook(false, null);
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(LocalUsersList.this, LoginActivity.class));
    }

    private void loadUsers() {
        List<Login> allLocalUsers = usersLocalRep.getAllLocalUsers();
        LocalUsersAdapter adapter = new LocalUsersAdapter(allLocalUsers, this);
        rv.setAdapter(adapter);
    }
}

