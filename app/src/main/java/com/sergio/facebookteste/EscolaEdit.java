package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Repository.EscolasRep;

public class EscolaEdit extends AppCompatActivity implements View.OnClickListener {

    EditText nomeInsert, imagemInsert, codigoPostalInsert, emailInsert, moradaInsert, telefoneInsert, latitudeInsert, longitudeInsert, loginInsert, passwordInsert, levelInsert;
    Button add, voltar;
    String oldNome;
    Escola escola;
    DatabaseHelper db;
    EscolasRep escolasRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edits_admin_config);

        db = new DatabaseHelper(getApplicationContext());
        escolasRep = new EscolasRep(db.openConnection());
        escola = new Escola();
        loadLayout();
        hideUnnecessaryEdits();
        Intent intent = getIntent();
        oldNome = intent.getStringExtra("escola");
        loadDataToEdit();
    }

    private void loadDataToEdit() {
        Escola escola = escolasRep.getEscola(oldNome);
        nomeInsert.setText(escola.getNome());
        imagemInsert.setText(escola.getImagem());
        codigoPostalInsert.setText(escola.getCodigoPostal());
        moradaInsert.setText(escola.getMorada());
        emailInsert.setText(escola.getEmail());
        telefoneInsert.setText(escola.getTelefone());
        latitudeInsert.setText(escola.getLatitude());
        longitudeInsert.setText(escola.getLongitude());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                confirmEdit();
                break;
            case R.id.voltar:
                voltar();
                break;
        }
    }

    private void confirmEdit() {
        if ((nomeInsert.length() != 0) && (imagemInsert.length() != 0) && (codigoPostalInsert.length() != 0) && (moradaInsert.length() != 0) && (emailInsert.length() != 0) && (telefoneInsert.length() != 0) && (latitudeInsert.length() != 0) && (longitudeInsert.length() != 0)) {
            escola.setNome(nomeInsert.getText().toString());
            escola.setImagem(imagemInsert.getText().toString());
            escola.setCodigoPostal(codigoPostalInsert.getText().toString());
            escola.setMorada(moradaInsert.getText().toString());
            escola.setEmail(emailInsert.getText().toString());
            escola.setTelefone(telefoneInsert.getText().toString());
            escola.setLatitude(latitudeInsert.getText().toString());
            escola.setLongitude(longitudeInsert.getText().toString());
            escolasRep.updateEscola(escola, oldNome);
            Toast.makeText(getBaseContext(), "Valores modificados com sucesso!", Toast.LENGTH_SHORT).show();
            voltar();
        }
    }

    private void loadLayout(){
        add = findViewById(R.id.add);
        voltar = findViewById(R.id.voltar);
        add.setText("Confirmar Edição");
        add.setOnClickListener(this);
        voltar.setOnClickListener(this);

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

    private void hideUnnecessaryEdits(){
        loginInsert.setVisibility(View.GONE);
        passwordInsert.setVisibility(View.GONE);
        levelInsert.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        voltar();
    }
    private void voltar() {
        finish();
    }
}