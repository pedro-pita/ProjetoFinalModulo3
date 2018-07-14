package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class LocalUsersDetails extends AppCompatActivity {

    TextView idTitulo, idView, nomeTitulo, nomeView, loginTitulo,loginView, passwordTitulo, passwordView, nivelTitulo, nivelView, idFacebookTitulo, idFacebookView;
    DatabaseHelper db;
    Login user;
    UsersLocalRep usersLocalRep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_from_lists);

        loadLayout();
        hideUnnecessaryEdits();
        loadData();
    }

    private void loadLayout(){
        idTitulo = (TextView) findViewById(R.id.idTitulo);
        idView = (TextView) findViewById(R.id.idView);
        nomeTitulo = (TextView) findViewById(R.id.nomeTitulo);
        nomeView = (TextView) findViewById(R.id.nomeView);
        loginTitulo = (TextView) findViewById(R.id.loginTitulo);
        loginView = (TextView) findViewById(R.id.loginView);
        passwordTitulo = (TextView) findViewById(R.id.passwordTitulo);
        passwordView = (TextView) findViewById(R.id.passwordView);
        nivelTitulo = (TextView) findViewById(R.id.nivelTitulo);
        nivelView = (TextView) findViewById(R.id.nivelView);
        idFacebookTitulo = (TextView) findViewById(R.id.idFacebookTitulo);
        idFacebookView = (TextView) findViewById(R.id.idFacebookView);
    }
    private void hideUnnecessaryEdits(){
        idFacebookView.setVisibility(View.GONE);
        idFacebookTitulo.setVisibility(View.GONE);
    }

    private void loadData() {
        db = new DatabaseHelper(getApplicationContext());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        user = usersLocalRep.getLocalUser(login);
        idView.setText(String.valueOf(user.getId()));
        nomeView.setText(user.getNome());
        loginView.setText(user.getLogin());
        passwordView.setText(user.getPassword());
        nivelView.setText(user.getLevel());
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
