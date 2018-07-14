package com.sergio.facebookteste.Model;

import java.io.Serializable;

public class Login implements Serializable {
    int id;
    String nome;
    String login;
    String password;
    String idFacebook;
    String url;
    String level;

    public Login(){
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

