package com.sergio.facebookteste.Repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Login;

import java.util.ArrayList;
import java.util.List;

public class UsersLocalRep {
    //Objeto de conexao com a DB
    private SQLiteDatabase conn;
    private DatabaseHelper db;

    public UsersLocalRep(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public void addLocalUser(String nome, String login, String password, String level) {
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("login", login);
        values.put("password", password);
        values.put("level", level);
        //Inserir cliente com controlo de excepcao, retorna id da chave criada(primary)
        //Tabelac permite null(indicar quais aceitam nulo), valores do content value
        int contaLocal_id = (int) conn.insertOrThrow("contaLocal", null, values);
        Log.i("ADD", "local_user_id: " + contaLocal_id);
    }

    public Login getLocalUser(String user) {

        Login login = new Login();
        String query = "SELECT * FROM contaLocal WHERE login = " + "'"+ user + "'";

        Log.e("GETCLIENTE", query);
        Cursor c = conn.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            login.setId(c.getInt(c.getColumnIndex("id")));
            login.setNome(c.getString(c.getColumnIndex("nome")));
            login.setLevel(c.getString(c.getColumnIndex("level")));
            login.setLogin(c.getString(c.getColumnIndex("login")));
            login.setPassword(c.getString(c.getColumnIndex("password")));
            return login;
        }
        return null;
    }

    public boolean verifyIfLocalUserExist(String user) {
        String query = "SELECT * FROM contaLocal WHERE login = " + "'"+ user + "'";

        Cursor c = conn.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            Log.e("ENCONTREI PORRA", query);
            return true;
        }
        Log.e("NAO ENCONTREI PORRA", query);
        return false;
    }

    public boolean getLocalUserToLogin(String user, String password){
        String query = "SELECT * FROM contaLocal WHERE login = " + "'" + user + "'" + " AND password = " + "'"+ password + "'";

        Cursor c = conn.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            Log.e("User encontrado", query);
            return true;
        }
        Log.e("User não encontrado", query);
        return false;
    }
    public List<Login> getAllLocalUsers() {
        List<Login> users = new ArrayList<Login>();
        String query = "SELECT * FROM contaLocal";

        Log.e("GETALL", query);

        Cursor c = conn.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Login login = new Login();
                login.setId(c.getInt(c.getColumnIndex("id")));
                login.setLogin(c.getString(c.getColumnIndex("login")));
                login.setNome(c.getString(c.getColumnIndex("nome")));
                login.setLevel(c.getString(c.getColumnIndex("level")));
                login.setPassword(c.getString(c.getColumnIndex("password")));
                users.add(login);
            } while (c.moveToNext());
        }
        return users;
    }

    public void removeLocalUser(String login){
        conn.delete("contaLocal","login" + " = ?",new String[]{String.valueOf(login)});
        Log.i("REM","login_user: " + login);
    }

    public void updateLocalUser(Login login, String oldLogin){
        ContentValues values = new ContentValues();
        values.put("nome",login.getNome());
        values.put("login",login.getLogin());
        values.put("password",login.getPassword());
        values.put("level",login.getLevel());
        //Tabela, valores do content value, clausula where, chave primarias
        //return com.update("cliente",values,"id"+" = ?", new String[] {String.valueOf(cliente.getId())})
        conn.update("contaLocal", values, "login"+" = ?", new String[] { String.valueOf(oldLogin)});
        Log.i("UPDATE", "user_login: " + login.getLogin());
    }

}
