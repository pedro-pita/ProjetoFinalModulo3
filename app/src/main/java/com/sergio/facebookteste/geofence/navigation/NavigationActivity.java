package com.sergio.facebookteste.geofence.navigation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sergio.facebookteste.Helper.DatabaseHelper;
import com.sergio.facebookteste.Model.Escola;
import com.sergio.facebookteste.Model.Login;
import com.sergio.facebookteste.Model.Session;
import com.sergio.facebookteste.R;
import com.sergio.facebookteste.Repository.EscolasRep;
import com.sergio.facebookteste.Repository.FavoritosRep;
import com.sergio.facebookteste.Repository.UsersLocalRep;
import com.sergio.facebookteste.geofence.MapsActivityGeo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap map;
    private LatLngBounds latlngBounds;
    private Button wayWalkBtn, wayDriveBtn,fences;
    private Polyline newPolyline;
    private boolean isTravelingTo = false;
    private int width, height;

    private LatLng device;
    private LatLng client;

    TextView distanceText, durationText, nomeView, moradaView,codigoView, telefoneView, emailView, idView;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest locationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    DatabaseHelper db;
    Intent intent;
    EscolasRep escolasRep;
    LatLng escolaCord;
    Escola escola;
    String nomeShow;
    ImageButton favorito, voltarBtn;
    Session ss;
    Boolean favOrNot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation);
        loadLayout();
        loadData();
        verifyFav();
        getSreenDimanstions();
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationRequest();
    }

    private void verifyFav() {
        try{
            ss = new Session(getApplicationContext());
            db = new DatabaseHelper(getApplicationContext());
            UsersLocalRep usersLocalRep = new UsersLocalRep(db.openConnection());
            Login login;
            login = usersLocalRep.getLocalUser(ss.getLogin());
            displayToast(" Id View " + idView.getText().toString() + " Id Login " + login.getId());
            FavoritosRep favoritosRep = new FavoritosRep(db.openConnection());
            boolean fav = favoritosRep.verifyIfFavExist(Integer.parseInt(idView.getText().toString()),login.getId());
            if(fav){
                favorito.setImageResource(R.drawable.favorito);
                favOrNot = true;
            }else{
                favorito.setImageResource(R.drawable.nao_favorito);
                favOrNot = false;
            }
        }catch (Exception e){
            //facebook user
            favorito.setVisibility(View.GONE);
        }
    }

    private void walkDirections() {
        if (!isTravelingTo) {
            isTravelingTo = true;
            findDirections(device.latitude, device.longitude,
                    client.latitude, client.longitude,
                    GMapV2Direction.MODE_WALKING);
        } else {
            isTravelingTo = false;
            findDirections(device.latitude, device.longitude,
                    client.latitude, client.longitude,
                    GMapV2Direction.MODE_WALKING);
        }
    }

    private void driveDirections() {
        if (!isTravelingTo) {
            isTravelingTo = true;
            findDirections(device.latitude, device.longitude,
                    client.latitude, client.longitude,
                    GMapV2Direction.MODE_DRIVING);
        } else {
            isTravelingTo = false;
            findDirections(device.latitude, device.longitude,
                    client.latitude, client.longitude,
                    GMapV2Direction.MODE_DRIVING);
        }
    }

    /*    Adicionar um marcador no mapa no local especificado. */
    private void addPlaceMarker(LatLng location, String title,
                                String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location).title(title);
        markerOptions.snippet(snippet);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(
                        BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(markerOptions);
    }
    /*
    É necessario definir um PendingIntent que inicia um IntentService,
    que ficará responsavel por
    lidar com as transições registadas no objeto Geofences
     */

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Toast.makeText(getApplicationContext(), "->" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Estás aqui!!!");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = map.addMarker(markerOptions);

                //onLocationChanged(locationResult.getLastLocation());

                //move map camera
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };
    @SuppressLint("MissingPermission")
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //1 metros
        locationRequest.setSmallestDisplacement(1);
        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            device = new LatLng(location.getLatitude(), location.getLongitude());
                            client = escolaCord;
                            LatLng point =
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude());
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(point)
                                    .zoom(12).build();
                            map.animateCamera(
                                    CameraUpdateFactory.newCameraPosition(position));
                        }
                    }
                });
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback, Looper.myLooper());
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    // Captura o resultado da direção
    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints, String value, String time) {
        //Linha que representará o caminho
        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                Color.RED);

        //Criação do caminho
        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null) {
            newPolyline.remove();
        }
        //Add ao mapa
        newPolyline = map.addPolyline(rectLine);
        if (isTravelingTo) {
            latlngBounds = createLatLngBoundsObject(device, client);
            map.animateCamera(CameraUpdateFactory
                    .newLatLngBounds(latlngBounds,
                            width, height, 150));
        }
        // Alteração do texto
        distanceText.setText(value);
        durationText.setText(time);
        Toast.makeText(NavigationActivity.this,"Distance: "+
                value + "\n Time:"+ time, Toast.LENGTH_SHORT).show();

    }

    //obter dimensões da tela
    private void getSreenDimanstions() {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        Toast.makeText(NavigationActivity.this,"->"+ width,
                Toast.LENGTH_SHORT).show();
        Log.d("error", "Dim->"+width);
    }

    // Criação de limites(Bounds)
    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    // Método responsavel por lançar a tarefa de captura do caminho
    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
                String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
                String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT,
                String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG,
                String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new
                GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        addPlaceMarker(escolaCord, nomeShow, null);

    }

    private void loadLayout() {
        favorito = (ImageButton) findViewById(R.id.favoritos);
        distanceText = (TextView) findViewById(R.id.distanceText);
        durationText = (TextView) findViewById(R.id.durationText);
        idView = (TextView) findViewById(R.id.idView);
        nomeView = (TextView) findViewById(R.id.nomeView);
        moradaView = (TextView) findViewById(R.id.moradaView);
        codigoView = (TextView) findViewById(R.id.codigoPostalView);
        telefoneView = (TextView) findViewById(R.id.telefoneView);
        emailView = (TextView) findViewById(R.id.emailView);
        wayDriveBtn = (Button) findViewById(R.id.wayDriveBtn);
        wayWalkBtn = (Button) findViewById(R.id.wayWalkBtn);
        wayWalkBtn = (Button) findViewById(R.id.wayWalkBtn);
        fences = (Button) findViewById(R.id.fences);
        voltarBtn = (ImageButton) findViewById(R.id.voltarBtn);
        voltarBtn.setOnClickListener(this);
        wayDriveBtn.setOnClickListener(this);
        wayWalkBtn.setOnClickListener(this);
        favorito.setOnClickListener(this);
        fences.setOnClickListener(this);
    }

    private void loadData() {
        intent = getIntent();
        db = new DatabaseHelper(getApplicationContext());
        String nomeEscola = intent.getStringExtra("nome");
        escolasRep = new EscolasRep(db.openConnection());
        escola = escolasRep.getEscola(nomeEscola);
        escolaCord = new LatLng(Double.parseDouble(escola.getLatitude()), Double.parseDouble(escola.getLongitude()));

        nomeShow = escola.getNome();
        idView.setText(String.valueOf(escola.getId()));
        nomeView.setText(escola.getNome());
        moradaView.setText(escola.getMorada());
        codigoView.setText(escola.getCodigoPostal());
        telefoneView.setText(escola.getTelefone());
        emailView.setText(escola.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wayWalkBtn:
                walkDirections();
                break;
            case R.id.wayDriveBtn:
                driveDirections();
                break;
            case R.id.fences:
                redirectToMapsActivityGeo();
                break;
            case R.id.voltarBtn:
                finish();
                break;
            case R.id.favoritos:
                if(favOrNot){
                    removeFav();
                    verifyFav();
                }else{
                    adicionarFav();
                    verifyFav();
                }
                break;
        }
    }

    private void redirectToMapsActivityGeo() {
        Intent intentMap = new Intent(NavigationActivity.this, MapsActivityGeo.class);
        intentMap.putExtra("nome", nomeView.getText().toString());
        startActivity(intentMap);
    }

    private void removeFav() {
        ss = new Session(getApplicationContext());
        db = new DatabaseHelper(getApplicationContext());
        UsersLocalRep usersLocalRep = new UsersLocalRep(db.openConnection());
        Login login;
        login = usersLocalRep.getLocalUser(ss.getLogin());
        FavoritosRep favoritosRep = new FavoritosRep(db.openConnection());
        favoritosRep.removeFav(Integer.parseInt(idView.getText().toString()),login.getId());
    }


    private void adicionarFav() {
        ss = new Session(getApplicationContext());
        db = new DatabaseHelper(getApplicationContext());
        UsersLocalRep usersLocalRep = new UsersLocalRep(db.openConnection());
        Login login;
        login = usersLocalRep.getLocalUser(ss.getLogin());
        displayToast(" Id View " + idView.getText().toString() + " Id Login " + login.getId());
        FavoritosRep favoritosRep = new FavoritosRep(db.openConnection());
        favoritosRep.addFavoritos(Integer.parseInt(idView.getText().toString()),login.getId());
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
