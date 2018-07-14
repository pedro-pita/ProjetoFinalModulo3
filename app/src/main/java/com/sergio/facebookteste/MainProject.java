package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class MainProject extends AppCompatActivity implements View.OnClickListener {

    private Button usersLocais, usersFacebook, escolas;
    Session ss;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        verificarPermissoes();
        setContentView(R.layout.main_project);
        usersLocais = (Button) findViewById(R.id.usersLocais);
        usersFacebook = (Button) findViewById(R.id.usersFacebook);
        escolas = (Button) findViewById(R.id.escolas);
        usersLocais.setOnClickListener(this);
        usersFacebook.setOnClickListener(this);
        escolas.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.escolas:
                redirectToEscolasList();
                break;
            case R.id.usersLocais:
                redirectToUsersLocaisList();
                break;
            case R.id.usersFacebook:
                redirectToUsersFacebookList();
                break;
        }
    }
    private void verificarPermissoes(){
        try {
            if(!(usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("1")) && (!(usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("2")))){
                Toast.makeText(getApplicationContext(), "Este user já não tem permissões para estar aqui!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainProject.this, EscolasList.class));
            }
        } catch (Exception e) {
            //Significa que é um user do facebook logo não tem permissoes
            finish();
            redirectToEscolasList();
        }
    }
    private void redirectToUsersFacebookList() {
        //finish();
        Intent intent = new Intent(MainProject.this, FacebookUsersList.class);
        startActivity(intent);
    }

    private void redirectToUsersLocaisList() {
        //finish();
        Intent intent = new Intent(MainProject.this, LocalUsersList.class);
        startActivity(intent);
    }

    private void redirectToEscolasList() {
        //finish();
        Intent intent = new Intent(MainProject.this, EscolasList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logout();
        return false;
    }

    private void logout(){
        ss.setLoggedin(false, null);
        ss.setLoggedinFacebook(false, null);
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(MainProject.this, LoginActivity.class));
    }
}
