package com.sergio.facebookteste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Repository.UsersLocalRep;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private TextView backToLogin;
    private EditText insertName, insertLogin, insertPassword;
    DatabaseHelper db;
    private UsersLocalRep usersLocalRep;
    Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        db = new DatabaseHelper(getApplicationContext());
        registerBtn = (Button) findViewById(R.id.registerBtn);
        backToLogin = (TextView) findViewById(R.id.backToLogin);
        insertName = (EditText) findViewById(R.id.nomeView);
        insertLogin = (EditText) findViewById(R.id.loginInsert);
        insertPassword = (EditText) findViewById(R.id.passwordInsert);

        registerBtn.setOnClickListener(this);
        backToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                register();
                break;
            case R.id.backToLogin:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void register() {
        String nome = insertName.getText().toString();
        String login = insertLogin.getText().toString();
        String pass = insertPassword.getText().toString();
        if (login.isEmpty() || pass.isEmpty() || nome.isEmpty()) {
            displayToast("Por favor, preencha todos os dados.");
        } else {
            usersLocalRep = new UsersLocalRep(db.openConnection());
            if (!usersLocalRep.verifyIfLocalUserExist(login)) {
                usersLocalRep.addLocalUser(nome, login, pass,"3");
                displayToast("Este utilizador registado com sucesso!");
                redirectToLoginActivity();
            } else {
                displayToast("Utilizador não foi registado, esta conta já existe!");
            }
        }
    }

    private void redirectToLoginActivity() {
        finish();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    private void mostrar(String user){
        usersLocalRep = new UsersLocalRep(db.openConnection());
        login = usersLocalRep.getLocalUser(user);
        displayToast("Nome:" + login.getNome() + "Login:" + login.getLogin() + "Password:" + login.getPassword());
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
