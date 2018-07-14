package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersFacebookRep;

public class FacebookUsersDetails extends AppCompatActivity {

    TextView idTitulo, idView, nomeTitulo, nomeView, loginTitulo,loginView, passwordTitulo, passwordView, nivelTitulo, nivelView, idFacebookTitulo, idFacebookView;
    DatabaseHelper db;
    Login user;
    UsersFacebookRep usersFacebookRep;
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
        loginView.setVisibility(View.GONE);
        passwordView.setVisibility(View.GONE);
        loginTitulo.setVisibility(View.GONE);
        passwordTitulo.setVisibility(View.GONE);
    }

    private void loadData() {
        db = new DatabaseHelper(getApplicationContext());
        usersFacebookRep = new UsersFacebookRep(db.openConnection());
        Intent intent = getIntent();
        String idFacebook = intent.getStringExtra("idFacebook");
        user = new Login();
        user = usersFacebookRep.getFacebookUser(idFacebook);
        idView.setText(String.valueOf(user.getId()));
        nomeView.setText(user.getNome());
        idFacebookView.setText(user.getIdFacebook());
        nivelView.setText(idFacebook);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
