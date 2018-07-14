package com.sergio.facebookteste.Model;

import android.content.Context;

import java.io.Serializable;

public class Escola implements Serializable {
    int id;
    String nome;
    String imagem;
    String morada;
    String codigoPostal;
    String telefone;
    String email;
    String latitude;
    String longitude;

    public Escola(){
    }
    public Escola(String nome, String imagem, String morada, String codigoPostal, String telefone, String email, String latitude, String longitude){
        this.nome = nome;
        this.imagem = imagem;
        this.morada = morada;
        this.codigoPostal = codigoPostal;
        this.telefone = telefone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}


