package com.sergio.facebookteste;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class LocalUsersAdd extends Activity implements View.OnClickListener {
    EditText nomeInsert, imagemInsert, codigoPostalInsert, emailInsert, moradaInsert, telefoneInsert, latitudeInsert, longitudeInsert, passwordInsert, loginInsert, levelInsert;
    Button add, voltar;
    String nome, login, password, level;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edits_admin_config);
        db = new DatabaseHelper(getApplicationContext());
        loadLayout();
        hideUnnecessaryEdits();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                adicionarUser();
                break;
            case R.id.voltar:
                voltar();
                break;
        }
    }

    private void hideUnnecessaryEdits(){
        imagemInsert.setVisibility(View.GONE);
        codigoPostalInsert.setVisibility(View.GONE);
        emailInsert.setVisibility(View.GONE);
        moradaInsert.setVisibility(View.GONE);
        latitudeInsert.setVisibility(View.GONE);
        longitudeInsert.setVisibility(View.GONE);
        telefoneInsert.setVisibility(View.GONE);
    }

    private void adicionarUser() {
        captureEdits();
        usersLocalRep = new UsersLocalRep(db.openConnection());
        if (!(usersLocalRep.verifyIfLocalUserExist(login))) {
            usersLocalRep.addLocalUser(nome, login, password, level);
            displayToast("User adicionar");
            voltar();
        } else {
            displayToast("User não adicionado, este user ja existe");
        }
    }

    private void captureEdits() {
        nome = nomeInsert.getText().toString();
        login = loginInsert.getText().toString();
        password = passwordInsert.getText().toString();
        level = levelInsert.getText().toString();

    }
    private void loadLayout(){
        add = findViewById(R.id.add);
        voltar = findViewById(R.id.voltar);
        nomeInsert = findViewById(R.id.nomeView);
        imagemInsert = findViewById(R.id.imagemInsert);
        codigoPostalInsert = findViewById(R.id.codigoPostalView);
        emailInsert = findViewById(R.id.emailInsert);
        moradaInsert = findViewById(R.id.moradaView);
        telefoneInsert = findViewById(R.id.telefoneView);
        latitudeInsert = findViewById(R.id.latitudeInsert);
        longitudeInsert = findViewById(R.id.longitudeInsert);
        passwordInsert = findViewById(R.id.passwordInsert);
        loginInsert = findViewById(R.id.loginInsert);
        levelInsert = findViewById(R.id.levelInsert);
        add.setOnClickListener(this);
        voltar.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        voltar();
    }
    private void voltar() {
        finish();
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
