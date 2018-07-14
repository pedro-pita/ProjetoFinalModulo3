package com.sergio.facebookteste.Repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;

import java.util.ArrayList;
import java.util.List;

public class FavoritosRep {
    private SQLiteDatabase conn;
    private DatabaseHelper db;

    public FavoritosRep(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public void addFavoritos(int userId, int escolaId) {
        ContentValues values = new ContentValues();
        values.put("idUser", userId);
        values.put("idEscola", escolaId);

        int id = (int) conn.insertOrThrow("favoritos", null, values);
        Log.i("ADD", "escola_nome: " + id);
    }

    public List<Escola> getAllUserFavoritos(int userId){
        List<Escola> fav = new ArrayList<Escola>();
        String query = "SELECT idEscola FROM favoritos WHERE idUser = " + "'" + userId + "'";
        Log.e("GETALL", query);
        int id;
        Cursor c = conn.rawQuery(query,null);
        if(c.moveToFirst()) {
            do {
                Escola escolaM = new Escola();
                id = c.getInt(c.getColumnIndex("idEscola"));
                String query2 = "SELECT * FROM escolas WHERE id = " + "'" + id + "'";
                Cursor c2 = conn.rawQuery(query2, null);
                Log.e("Query 2", query2);
                escolaM.setId(c2.getInt(c2.getColumnIndex("idEscola")));
                escolaM.setNome(c2.getString(c2.getColumnIndex("nome")));
                escolaM.setImagem(c2.getString(c2.getColumnIndex("imagem")));
                escolaM.setMorada(c2.getString(c2.getColumnIndex("morada")));
                escolaM.setCodigoPostal(c2.getString(c2.getColumnIndex("codigoPostal")));
                escolaM.setTelefone(c2.getString(c2.getColumnIndex("telefone")));
                escolaM.setEmail(c2.getString(c2.getColumnIndex("email")));
                escolaM.setLatitude(c2.getString(c2.getColumnIndex("latitude")));
                escolaM.setLongitude(c2.getString(c2.getColumnIndex("longitude")));
                fav.add(escolaM);
            } while (c.moveToNext());
        }
        return fav;
    }

    /*public List<Escola> getAllUserFavoritos(int userId){
        List<Escola> fav = new ArrayList<Escola>();
        Escola escolaD = new Escola();
        String query = "SELECT escolas.id FROM ((favoritos INNER JOIN escolas ON favoritos.idEscola = escolas.id) INNER JOIN contaLocal ON favoritos.idUser = contaLocal.id WHERE favoritos.idUser = " + "'" + userId + "'";
        Log.e("GETESCOLA", query);
        Cursor c = conn.rawQuery(query, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            escolaD.setId(c.getInt(c.getColumnIndex("id")));
            fav.add(escolaD);
        }while (c.moveToNext());
        return fav;
    }*/
    public boolean verifyIfFavExist(int idUser, int idEscola) {
        String query = "SELECT * FROM favoritos WHERE idUser = " + idUser + " AND idEscola = " + idEscola;

        Cursor c = conn.rawQuery(query, null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            Log.e("ENCONTREI ", query);
            return true;
        }
        Log.e("NAO ENCONTREI", query);
        return false;
    }

    public void removeFav(int idUser, int idEscola){
            conn.delete("favoritos",
                    "idUser" + " = ? AND " + "idEscola" + " = ?",
                    new String[] {String.valueOf(idUser), idEscola+""});
    }
}
