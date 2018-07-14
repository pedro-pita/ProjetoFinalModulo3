package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class LocalUsersEdit extends AppCompatActivity implements View.OnClickListener {

    EditText nomeInsert, imagemInsert, codigoPostalInsert, emailInsert, moradaInsert, telefoneInsert, latitudeInsert, longitudeInsert, loginInsert, passwordInsert, levelInsert;
    Button add, voltar;
    String oldLogin;
    Login login;
    DatabaseHelper db;
    UsersLocalRep usersLocalRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edits_admin_config);

        db = new DatabaseHelper(getApplicationContext());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        login = new Login();
        loadLayout();
        hideUnnecessaryEdits();
        Intent intent = getIntent();
        oldLogin = intent.getStringExtra("user");
        loadDataToEdit();
    }

    private void loadDataToEdit() {
        Login user = usersLocalRep.getLocalUser(oldLogin);
        if(user.getLogin().equals("root")){
            displayToast("Desculpe mas este utilizador não pode ser editado");
            voltar();
        }else {
            nomeInsert.setText(user.getNome());
            loginInsert.setText(user.getLogin());
            passwordInsert.setText(user.getPassword());
            levelInsert.setText(user.getLevel());
        }
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
        if ((nomeInsert.length() != 0) && (loginInsert.length() != 0) && (passwordInsert.length() != 0)) {
            login.setNome(nomeInsert.getText().toString());
            login.setLogin(loginInsert.getText().toString());
            login.setPassword(passwordInsert.getText().toString());
            login.setLevel(levelInsert.getText().toString());
            usersLocalRep.updateLocalUser(login, oldLogin);
            Toast.makeText(getBaseContext(), "Valores modificados com sucesso!", Toast.LENGTH_SHORT).show();
            voltar();
        }else{
            displayToast("Preencha todos os dados");
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
        imagemInsert.setVisibility(View.GONE);
        codigoPostalInsert.setVisibility(View.GONE);
        emailInsert.setVisibility(View.GONE);
        moradaInsert.setVisibility(View.GONE);
        latitudeInsert.setVisibility(View.GONE);
        longitudeInsert.setVisibility(View.GONE);
        telefoneInsert.setVisibility(View.GONE);
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
