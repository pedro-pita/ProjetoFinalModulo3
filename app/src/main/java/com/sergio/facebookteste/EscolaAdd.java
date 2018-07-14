package com.sergio.facebookteste;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Repository.EscolasRep;

public class EscolaAdd extends Activity implements View.OnClickListener {
    EditText nomeInsert, imagemInsert, codigoPostalInsert, emailInsert, moradaInsert, telefoneInsert, latitudeInsert, longitudeInsert, loginInsert, passwordInsert, levelInsert;
    Button add, voltar;
    String nome, imagem, codigoPostal, email, morada;
    String telefone;
    String latitude, longitude;
    DatabaseHelper db;
    EscolasRep escolasRep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edits_admin_config);
        db = new DatabaseHelper(getApplicationContext());
        loadLayout();
        hideUnnecessaryEdits();
        add.setOnClickListener(this);
        voltar.setOnClickListener(this);
    }

    private void adicionarEscola() {
        captureEdits();
        escolasRep = new EscolasRep(db.openConnection());
        if (!(escolasRep.verifyIfEscolaExist(nome))) {
            escolasRep.addEscola(nome, imagem, morada, codigoPostal, telefone, email, latitude, longitude);
            displayToast("Esta escola foi adicionada com sucesso");
            voltar();
        } else {
            displayToast("Esta escola ja existe");
        }
    }

    private void loadLayout() {
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
        loginInsert = findViewById(R.id.loginInsert);
        passwordInsert = findViewById(R.id.passwordInsert);
        levelInsert = findViewById(R.id.levelInsert);
    }

    private void captureEdits() {
        nome = nomeInsert.getText().toString();
        imagem = imagemInsert.getText().toString();
        codigoPostal = codigoPostalInsert.getText().toString();
        email = emailInsert.getText().toString();
        morada = moradaInsert.getText().toString();
        telefone = telefoneInsert.getText().toString();
        latitude = latitudeInsert.getText().toString();
        longitude = longitudeInsert.getText().toString();
    }

    private void hideUnnecessaryEdits(){
        loginInsert.setVisibility(View.GONE);
        passwordInsert.setVisibility(View.GONE);
        levelInsert.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                adicionarEscola();
                break;
            case R.id.voltar:
                voltar();
                break;
        }
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