package com.sergio.facebookteste.geofence;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Repository.EscolasRep;
import com.sergio.facebookteste.R;

public class MapsActivityGeo extends FragmentActivity
        implements View.OnClickListener,
        OnMapReadyCallback,
        android.content.DialogInterface.OnClickListener {

    public static final String TAG = MapsActivityGeo.class.getSimpleName();

    private GoogleMap googleMap;
    private MyPlaces client;
    private List<Geofence> myFences = new ArrayList<Geofence>();
    private int status;

    TextView nomeView, moradaView,codigoView, telefoneView, emailView, idView;
    DatabaseHelper db;
    Intent intent;
    EscolasRep escolasRep;
    LatLng escolaCord;
    Escola escola;
    String nomeShow;
    Double latitude;
    Double longitude;
    String morada;
    String nomeEscola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitymapsgeo);
        loadData();
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton clientBtn = (ImageButton) findViewById(R.id.ib_client);
        clientBtn.setOnClickListener(this);

        ImageButton resetBtn = (ImageButton) findViewById(R.id.ib_reset);
        resetBtn.setOnClickListener(this);

        ImageButton voltarBtn = (ImageButton) findViewById(R.id.voltarBtn);
        voltarBtn.setOnClickListener(this);

        setUpMapIfNeeded();

        if (verifyConnection()) {
            Toast.makeText(getApplicationContext(), "ONLINE!",
                    Toast.LENGTH_LONG).show();
            status = 1;
        } else {
            setMobileDataEnabledAlert();
            status = 0;
        }

    }
    private void loadData() {
        intent = getIntent();
        db = new DatabaseHelper(getApplicationContext());
        nomeEscola = intent.getStringExtra("nome");
        escolasRep = new EscolasRep(db.openConnection());
        escola = escolasRep.getEscola(nomeEscola);
        latitude = Double.parseDouble(escola.getLatitude());
        longitude = Double.parseDouble(escola.getLongitude());
        morada = escola.getMorada();
    }

    /*
     * Verificação da ligação à rede wi-fi ou dados
     * Add no manifesto
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public boolean verifyConnection() {
        boolean isConnect;
        // Gestor de conetividade
        ConnectivityManager conectivtyManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            isConnect = true;
        } else {
            isConnect = false;
        }
        return isConnect;
    }
    private void setMobileDataEnabledAlert() {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage(
                        "Conectivity off \n Are you sure you want " +
                                "to activated conectivity?")
                .setIcon(R.drawable.seta).setTitle("Destino que pretende")
                .setPositiveButton("Yes", this)
                .setNegativeButton("No", this)
                .setCancelable(true).create();
        ad.show();
    }
    private void setMobileDataEnabled() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if (!wifiEnabled) {
            if (wifiManager.setWifiEnabled(true)) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Conectivity Activated!", Toast.LENGTH_LONG);
                LinearLayout linearLayout = (LinearLayout) toast.getView();
                TextView messageTextView = (TextView) linearLayout
                        .getChildAt(0);
                messageTextView.setTextSize(25);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else
            Toast.makeText(getApplicationContext(), "ATIVE!",
                    Toast.LENGTH_LONG)
                    .show();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: // yes
                setMobileDataEnabled();
                status = 1;
                break;
            case DialogInterface.BUTTON_NEGATIVE: // no
                status = 0;
                break;
            default:
                // nothing
                break;
        }

    }

    @Override
    public void onClick(View v) {

        MyPlaces place;
        switch (v.getId()) {
            case R.id.ib_client:
                Toast.makeText(this, "You Clicked CLIENT",
                        Toast.LENGTH_SHORT)
                        .show();
                place = client;
                moveToLocation(place);
                break;
            case R.id.ib_reset:
                Toast.makeText(this, "Resetting Our Map",
                        Toast.LENGTH_SHORT)
                        .show();
                googleMap.clear();
                myFences.clear();
                setUpMap();
                break;
            case R.id.voltarBtn:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        this.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i(TAG, "Cleanup Our Fields");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpMapIfNeeded() {
        // verifica se o map já foi instanciado
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment)
                    getSupportFragmentManager()
                            .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        // Em caso de sucesso recupera o mapa
        if (googleMap != null) {
            setUpMap();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        googleMap.setTrafficEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        double latReturned = 32.662739;
        double longReturned = -16.922487;

        double latClientReturned = latitude;
        double longClientReturned = longitude;
        String nameClientReturned = nomeEscola;

        // add um local Geofence
        client = new MyPlaces(nameClientReturned,
                morada, new LatLng(
                latClientReturned, longClientReturned),
                200, 12,
                R.drawable.seta);
        addPlaceMarker(client);
        addFence(client);

        /* Definir  um local como padrão e posicione lá    */
        moveToLocation(client);
    }

    /*    Adicionar um marcador no mapa no local especificado. */
    private void addPlaceMarker(MyPlaces place) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place.getCoordinates())
                .title(place.getTitle());
        if (!TextUtils.isEmpty(place.getSnippet())) {
            markerOptions.snippet(place.getSnippet());
        }
        if (place.getIconResourceId() > 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(place
                    .getIconResourceId()));
        }
        googleMap.addMarker(markerOptions);
        drawGeofenceAroundTarget(place);
    }

    /* Se local tiver um raio de limitação(fences) maior que 0,
     desenhar um círculo a volta.     */
    private void drawGeofenceAroundTarget(MyPlaces place) {
        if (place.getFenceRadius() <= 0) {
            // Nothing to draw
            return;
        }
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(place.getCoordinates());
        circleOptions.fillColor(Color.argb(0x55,
                0x00, 0x00, 0xff));
        circleOptions.strokeColor(Color.argb(0xaa,
                0x00, 0x00, 0xff));
        circleOptions.strokeWidth(1);
        circleOptions.radius(place.getFenceRadius());
        googleMap.addCircle(circleOptions);
    }

    /* Atualiza a visualização para um local especifico    */
    private void moveToLocation(final MyPlaces place) {
        // Mover a camara para a posição 'place' com zoom 5
        // Desloque até o novo local e defina o nível de zoom
        // para o esse lugar.
        googleMap.animateCamera(
                CameraUpdateFactory.newLatLng(place.getCoordinates()),
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        googleMap.animateCamera(
                                CameraUpdateFactory.zoomTo(14),
                                2000, null);
                    }
                    @Override
                    public void onCancel() {
                        // Rotina de cancelamento
                    }
                });
    }

    /*
     Se o  local tiver um raio de limitação > 0, adicione-o às
     limitações(fences) monitorizadas.
     */
    private void addFence(MyPlaces place) {
        if (place.getFenceRadius() <= 0) {
            // Nothing to monitor
            return;
        }
        Geofence geofence = new Geofence.Builder()
                .setCircularRegion(place.getCoordinates().latitude,
                        place.getCoordinates().longitude,
                        place.getFenceRadius())
                .setRequestId(place.getTitle())
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build();
        myFences.add(geofence);
    }
    @Override
    public void onBackPressed() {
        voltar();
    }
    private void voltar() {
        finish();
    }
}

