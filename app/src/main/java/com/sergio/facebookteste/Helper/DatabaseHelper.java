package com.sergio.facebookteste.Helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Nome da base de dados
    private static final String DATABASE_NAME = "escola";

    //Versao da base de dados
    private static final int DATABASE_VERSION = 60;

    //Nome tabelas
    private static final String TABLE_CONTA_FACE = "contaFace";
    private static final String TABLE_CONTA_LOCAL = "contaLocal";
    private static final String TABLE_ESCOLAS = "escolas";
    private static final String TABLE_USERS_ESCOLAS = "favoritos";

    //Nomes das colunas
    //Colunas da tabela das contas Facebook
    private static final String KEY_ID = "id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_ID_FACEBOOK = "idFacebook";
    private static final String KEY_ID_LEVEL = "level";
    private static final String KEY_URL = "url";

    //Colunas da tabela das contas Locais
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    //Colunas da tabela escolas
    private static final String KEY_IMAGEM = "imagem";
    private static final String KEY_MORADA = "morada";
    private static final String KEY_CODIGO_POSTAL = "codigoPostal";
    private static final String KEY_TELEFONE = "telefone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    //Colunas da tabela favoritos
    private static final String KEY_ID_FAVORITO = "idFavorito";
    private static final String KEY_ID_USER = "idUser";
    private static final String KEY_ID_ESCOLA = "idEscola";

    //Tag para o LogCat
    private static final String LOG ="DatabaseHelper";

    //Instrucao SQL para a criacao da tabela para contas facebook
    private static final String CREATE_TABLE_CONTA_FACE = "CREATE TABLE "
            + TABLE_CONTA_FACE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + KEY_NOME + " VARCHAR(100), "
            + KEY_URL + " VARCHAR(100), "
            + KEY_ID_LEVEL + " VARCHAR(7), "
            + KEY_ID_FACEBOOK + " VARCHAR(100)" + ")";

    //Instrucao SQL para a criacao da tabela para contas locais
    private static final String CREATE_TABLE_CONTA_LOCAL = "CREATE TABLE "
            + TABLE_CONTA_LOCAL + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + KEY_LOGIN + " VARCHAR(100), "
            + KEY_NOME + " VARCHAR(100), "
            + KEY_ID_LEVEL + " VARCHAR(7), "
            + KEY_PASSWORD + " VARCHAR(100)" + ")";

    //Instrucao SQL para a criacao da tabela para as escolas
    private static final String CREATE_TABLE_ESCOLA = "CREATE TABLE "
            + TABLE_ESCOLAS + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + KEY_NOME + " VARCHAR(100), "
            + KEY_IMAGEM + " VARCHAR(100), "
            + KEY_MORADA + " VARCHAR(100), "
            + KEY_CODIGO_POSTAL + " VARCHAR(100), "
            + KEY_TELEFONE + " VARCHAR(100), "
            + KEY_EMAIL + " VARCHAR(100), "
            + KEY_LATITUDE + " VARCHAR(100), "
            + KEY_LONGITUDE + " VARCHAR(100)" + ")";

    //Instrucao SQL para a criacao da tabela favoritos (tabela que é criada apartir da relação de muitos para muitos da tabela users coma  tabela escolas
    private static final String CREATE_TABLE_USERS_ESCOLAS = "CREATE TABLE " + TABLE_USERS_ESCOLAS
            + "(" + KEY_ID_FAVORITO + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + KEY_ID_USER + " INTEGER,"
            + KEY_ID_ESCOLA + " INTEGER" + ")";

    private Context ctx;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.ctx = context;
    }

    public DatabaseHelper(Context applicationContext){
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = applicationContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Criacao das tableas
        db.execSQL(CREATE_TABLE_CONTA_FACE);
        db.execSQL(CREATE_TABLE_CONTA_LOCAL);
        db.execSQL(CREATE_TABLE_ESCOLA);
        db.execSQL(CREATE_TABLE_USERS_ESCOLAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Remocao das tabelas existentes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTA_FACE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTA_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESCOLAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_ESCOLAS);
        //Criacao das novas tableas
        onCreate(db);
    }

    public SQLiteDatabase openConnection() {
        try{
            SQLiteDatabase conndb = this.getReadableDatabase();
            Log.i("OK", "Conectado");
            return conndb;
        }catch(SQLException ex){
            Log.e("ERRO", ex.toString());
        }
        return null;
    }
        }