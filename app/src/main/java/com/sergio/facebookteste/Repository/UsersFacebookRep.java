package com.sergio.facebookteste.Repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;

import java.util.ArrayList;
import java.util.List;

public class UsersFacebookRep {
    //Objeto de conexao com a DB
    private SQLiteDatabase conn;
    private DatabaseHelper db;

    public UsersFacebookRep(SQLiteDatabase conn) {
        this.conn = conn;
    }

    //FACEBOOK ACCOUNTS
    public void addFacebookUser(String nome, String idFacebook, String url, String level) {
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("idFacebook", idFacebook);
        values.put("url", url);
        values.put("level", level);
        //Inserir cliente com controlo de excepcao, retorna id da chave criada(primary)
        //Tabelac permite null(indicar quais aceitam nulo), valores do content value
        int contaFace_id = (int) conn.insertOrThrow("contaFace", null, values);
        Log.i("ADD", "facebook_user_id: " + contaFace_id);
    }

    public Login getFacebookUser(String id) {
        Login login = new Login();
        String query = "SELECT * FROM contaFace WHERE idFacebook = " + "'" + id + "'";
        Log.e("GETCLIENTE", query);
        Cursor c = conn.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {
            Log.e("ENCONTREI", query);
            c.moveToFirst();
            login.setId(c.getInt(c.getColumnIndex("id")));
            login.setNome(c.getString(c.getColumnIndex("nome")));
            login.setLevel(c.getString(c.getColumnIndex("level")));
            login.setUrl(c.getString(c.getColumnIndex("url")));
            login.setIdFacebook(c.getString(c.getColumnIndex("idFacebook")));
            return login;
        }
        Log.e("SOU NULL", query);
        return null;
    }

    public boolean verifyIfFacebookUserExist(String id) {
        String query = "SELECT * FROM contaFace WHERE idFacebook = " + "'" + id + "'";

        Cursor c = conn.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            Log.e("ENCONTREI", query);
            return true;
        }
        Log.e("NAO ENCONTREI", query);
        return false;
    }
    public void removeFacebookUser(String id){
        conn.delete("contaFace","idFacebook" + " = ?",new String[]{String.valueOf(id)});
        Log.i("REM","user id: " + id);
    }

    public List<Login> getAllFacebookUsers() {
        List<Login> users = new ArrayList<Login>();
        String query = "SELECT * FROM contaFace";

        Log.e("GETALL", query);

        Cursor c = conn.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Login login = new Login();
                login.setId(c.getInt(c.getColumnIndex("id")));
                login.setNome(c.getString(c.getColumnIndex("nome")));
                login.setLevel(c.getString(c.getColumnIndex("level")));
                login.setUrl(c.getString(c.getColumnIndex("url")));
                login.setIdFacebook(c.getString(c.getColumnIndex("idFacebook")));
                users.add(login);
            } while (c.moveToNext());
        }
        return users;
    }
}
