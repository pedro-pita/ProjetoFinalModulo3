package com.sergio.facebookteste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.Repository.UsersFacebookRep;
import com.sergio.facebookteste.Repository.UsersLocalRep;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends Activity implements View.OnClickListener {

    private final String TAG = "FACEBOOK";

    //Facebook
    private LoginButton loginBtnFace;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    //DB
    DatabaseHelper db;
    private UsersFacebookRep usersFacebookRep;
    private UsersLocalRep usersLocalRep;

    //layout
    private TextView userName, userStatus;
    EditText loginInsert, passInsert;
    Button loginBtn, registerBtn;

    //variaveis
    String loginEdit, passwordEdit;

    //sharedPref
    Session ss;
    CheckBox shared;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        db = new DatabaseHelper(getApplicationContext());
        ss = new Session(getApplicationContext());
        setContentView(R.layout.login_activity);
        loadLayout();
        loginBtnFace = (LoginButton) findViewById(R.id.fb_login_button);

        loginBtnFace.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        loginBtnFace.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {
                                Bundle facebookData = getFacebookData(jsonObject);
                                String url = facebookData.getString("profile_pic").toString();
                                String nome = facebookData.getString("first_name").toString() + " " + facebookData.getString("last_name").toString();
                                addFacebookUserToDataBase(loginResult, nome, url);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                displayToast("Login Cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                Log.d(TAG, "Login attempt failed.");
                deleteAccessToken();
            }
        });
        if (!accessTokenTracker.isTracking()) {
            displayToast("");
        }
    }

    private void addFacebookUserToDataBase(LoginResult loginResult, String nome, String url) {
        String idFacebook = loginResult.getAccessToken().getUserId().toString();
        usersFacebookRep = new UsersFacebookRep(db.openConnection());
        if (usersFacebookRep.verifyIfFacebookUserExist(idFacebook) == false) {
            usersFacebookRep.addFacebookUser(nome, idFacebook, url, "3");
            Toast.makeText(getApplicationContext(), "Utilizador não existente!", Toast.LENGTH_SHORT).show();
            //mostrar(loginResult);
        } else {
            Toast.makeText(getApplicationContext(), "Utilizador ja existente!", Toast.LENGTH_SHORT).show();
        }
        ss.setLoggedinFacebook(true, idFacebook);
        redirectToMainProject();
    }

    private void loadLayout() {
        loginInsert = findViewById(R.id.loginInsert);
        passInsert = findViewById(R.id.passwordInsert);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    private void tryLogin() {
        captureEdits();
        displayToast("Login: " + loginEdit + " Pass:" + passwordEdit);
        if (loginEdit.isEmpty() || passwordEdit.isEmpty()) {
            displayToast("Por Favor preencha todos os dados!");
        } else {
            usersLocalRep = new UsersLocalRep(db.openConnection());
            if (usersLocalRep.getLocalUserToLogin(loginEdit, passwordEdit)) {
                shared = (CheckBox) findViewById(R.id.stayLogged);
                savePreferences(loginEdit);
                verificarPermissoes();
            } else {
                displayToast("Palavra pass ou login incorretos");
            }
        }
    }

    private void savePreferences(String loginEdit) {
        if (shared.isChecked()) {
            ss.setLoggedin(true, loginEdit);
        } else {
            ss.setLoggedin(false, loginEdit);
        }
    }

    private void verificarPermissoes() {
        try {
            if ((usersLocalRep.getLocalUser(ss.getLogin()).getLevel().equals("1"))) {
                redirectToMainProject();
            }else{
                redirectToEscolasList();
            }
        } catch (Exception e) {
            //Significa que é um user do facebook logo não tem permissoes
            finish();
            redirectToEscolasList();
        }
    }

    private void redirectToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void redirectToEscolasList(){
        finish();
        startActivity(new Intent(LoginActivity.this, EscolasList.class));
    }

    private void redirectToMainProject() {
        finish();
        startActivity(new Intent(LoginActivity.this, MainProject.class));
    }

    private void captureEdits() {
        loginEdit = loginInsert.getText().toString();
        passwordEdit = passInsert.getText().toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    //User logged out
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id
                        + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : " + e.toString());
        }
        return bundle;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        deleteAccessToken();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        deleteAccessToken();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                tryLogin();
                break;
            case R.id.registerBtn:
                redirectToRegister();
                break;
        }
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
