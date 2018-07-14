package com.sergio.facebookteste.Repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;

import java.util.ArrayList;
import java.util.List;

public class EscolasRep {

    private SQLiteDatabase conn;
    private DatabaseHelper db;

    public EscolasRep(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public void addEscola(String nome, String imagem, String morada, String codigoPostal, String telefone, String email, String latitude, String longitude) {
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("imagem", imagem);
        values.put("morada", morada);
        values.put("codigoPostal", codigoPostal);
        values.put("telefone", telefone);
        values.put("email", email);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        //Inserir cliente com controlo de excepcao, retorna id da chave criada(primary)
        //Tabelac permite null(indicar quais aceitam nulo), valores do content value
        int escola_nome = (int) conn.insertOrThrow("escolas", null, values);
        Log.i("ADD", "escola_nome: " + escola_nome);
    }

    public Escola getEscola(String escola) {

        Escola escolaD = new Escola();
        String query = "SELECT * FROM escolas WHERE nome = " + "'"+ escola + "'";

        Log.e("GETESCOLA", query);
        Cursor c = conn.rawQuery(query, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            escolaD.setId(c.getInt(c.getColumnIndex("id")));
            escolaD.setNome(c.getString(c.getColumnIndex("nome")));
            escolaD.setImagem(c.getString(c.getColumnIndex("imagem")));
            escolaD.setMorada(c.getString(c.getColumnIndex("morada")));
            escolaD.setCodigoPostal(c.getString(c.getColumnIndex("codigoPostal")));
            escolaD.setTelefone(c.getString(c.getColumnIndex("telefone")));
            escolaD.setEmail(c.getString(c.getColumnIndex("email")));
            escolaD.setLatitude(c.getString(c.getColumnIndex("latitude")));
            escolaD.setLongitude(c.getString(c.getColumnIndex("longitude")));
            return escolaD;
        }
        return null;
    }

    public boolean verifyIfEscolaExist(String escola) {
        String query = "SELECT * FROM escolas WHERE nome = " + "'"+ escola + "'";

        Cursor c = conn.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            Log.e("ENCONTREI PORRA", query);
            return true;
        }
        Log.e("NAO ENCONTREI PORRA", query);
        return false;
    }

    public void removeEscola(String escola){
        conn.delete("escolas","nome" + " = ?",new String[]{String.valueOf(escola)});
        Log.i("REM","escola_name: " + escola);
    }

    public void updateEscola(Escola escola, String oldNome){
        ContentValues values = new ContentValues();
        values.put("nome",escola.getNome());
        values.put("email",escola.getEmail());
        values.put("codigoPostal",escola.getCodigoPostal());
        values.put("imagem",escola.getImagem());
        values.put("latitude",escola.getLatitude());
        values.put("longitude",escola.getLongitude());
        values.put("telefone",escola.getTelefone());
        values.put("morada",escola.getMorada());
        //Tabela, valores do content value, clausula where, chave primarias
        //return com.update("cliente",values,"id"+" = ?", new String[] {String.valueOf(cliente.getId())})
        conn.update("escolas", values, "nome"+" = ?", new String[] { String.valueOf(oldNome)});
        Log.i("UPDATE", "escola_name: " + escola.getNome());
    }

    public List<Escola> getAllEscolas(){
        String query;
        List<Escola> escolas = new ArrayList<Escola>();
        query = "SELECT * FROM escolas";
        Log.e("GETALL", query);

        Cursor c = conn.rawQuery(query,null);

        if(c.moveToFirst()) {
            do {
                Escola user = new Escola();
                user.setId(c.getInt(c.getColumnIndex("id")));
                user.setNome(c.getString(c.getColumnIndex("nome")));
                user.setImagem(c.getString(c.getColumnIndex("imagem")));
                escolas.add(user);
            } while (c.moveToNext());
        }
        return escolas;
    }
}
