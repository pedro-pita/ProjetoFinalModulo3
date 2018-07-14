package com.sergio.facebookteste;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.EscolasRep;
import com.sergio.facebookteste.Repository.UsersLocalRep;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class EscolasList extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    Context ctx;
    RecyclerView rv;
    DatabaseHelper db;
    EscolasRep escola;
    List<Escola> escolas;
    Session ss;
    EscolasRep escolasRep;
    UsersLocalRep usersLocalRep;

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL, null, this, this);
    private static String BASE_URL = "https://www.dropbox.com/s/4jq9llty1mwzhkb/escolasJson.json?dl=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        db = new DatabaseHelper(getApplicationContext());
        ctx = this;
        ss = new Session(getApplicationContext());
        escola = new EscolasRep(db.openConnection());
        usersLocalRep = new UsersLocalRep(db.openConnection());
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        getData();
    }
    public void getData() {
        JsonTarefaImportEscola task = new JsonTarefaImportEscola();
        task.execute();
    }
    @Override
    public void onResponse(JSONObject response) {
        escolas = new ArrayList<Escola>();
        List<Escola> listaI = new ArrayList<Escola>();
        try {
            JSONObject jsonEscolas = response.getJSONObject("escolas");
            JSONArray jsonEscola = jsonEscolas.getJSONArray("escola");

            for (int i = 0; i < jsonEscola.length(); i++) {
                JSONObject jsonEscolaItem = jsonEscola.getJSONObject(i);
                String nome = jsonEscolaItem.getString("nome");
                String imagem = jsonEscolaItem.getString("imagem");
                String morada = jsonEscolaItem.getString("morada");
                String codigoPostal = jsonEscolaItem.getString("codigo-postal");
                String telefone = jsonEscolaItem.getString("telefone");
                String email = jsonEscolaItem.getString("email");
                String latitude = jsonEscolaItem.getString("latitude");
                String longitude = jsonEscolaItem.getString("longitude");
                escolasRep = new EscolasRep(db.openConnection());
                listaI = escolasRep.getAllEscolas();
                Log.d(TAG, jsonEscolaItem.getString("nome"));
                if (listaI.size() < jsonEscola.length()) {
                    Log.d("CREATE", "ESCOLAS");
                    escolasRep.addEscola(nome, imagem, morada, codigoPostal, telefone, email, latitude, longitude);
                }
                Escola escolaT = new Escola(nome, imagem, morada, codigoPostal, telefone, email, latitude, longitude);
                escolas.add(escolaT);
            }
            escolas = escolasRep.getAllEscolas();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage().toString());
        }
        EscolasAdapter adapter = new EscolasAdapter(escolas,this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error!" + error, Toast.LENGTH_SHORT).show();
        Log.d(TAG, String.valueOf(error));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class JsonTarefaImportEscola extends AsyncTask<String, Void, Escola> {
        @Override
        protected Escola doInBackground(String... params) {
            Escola escolaF = new Escola();
            RequestQueue queue = Volley.newRequestQueue(ctx);
            queue.add(jsonObjectRequest);
            return escolaF;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         1- pode fazer logout, ver favoritos, editar e remover (administradores e Super utilizador)
         2- pode fazer logout e ver favoritos (Utilizadores locais com nivel3(criados no registerActivity)
         3- só pode fazer logout(facebook users)
         4- Erro ao verificação (resoltado que eu não estava a espera)
        */
        if (verificarPermissões() == 1) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        } else if (verificarPermissões() == 2) {
            getMenuInflater().inflate(R.menu.fav, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.logout, menu);
            return true;
        }
    }

    private int verificarPermissões(){
        /*
         1- pode fazer logout, ver favoritos, editar e remover (administradores e Super utilizador)
         2- pode fazer logout e ver favoritos (Utilizadores locais com nivel3(criados no registerActivity)
         3- só pode fazer logout(facebook users)
         4- Erro na verificação (resultado que eu não estava a espera)
        */
        if(isLoggedIn()){
            return 3; //utilizador facebook
        }else{
            try {
                displayToast(usersLocalRep.getLocalUser(ss.getLogin()).getLevel());
                if((usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("1"))){
                    return 1;
                }else if(usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("2")){
                    return 2;
                }
            } catch (Exception e) {
                return 4; //erro de verificação
            }
        }
        return 4;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                redirectToEscolasAdminConfig();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.favoritos:
                startActivity(new Intent(EscolasList.this, FavoritosList.class));
                break;
        }
        return false;
    }

    private void redirectToEscolasAdminConfig() {
        startActivity(new Intent(EscolasList.this, EscolaAdd.class));
    }

    private void logout(){
        ss.setLoggedin(false, null);
        ss.setLoggedinFacebook(false, null);
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(EscolasList.this, LoginActivity.class));
    }
    private boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }
    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
