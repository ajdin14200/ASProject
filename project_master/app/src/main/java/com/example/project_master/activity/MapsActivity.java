package com.example.project_master.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

// a finir : gerer les checkpoint, gerer les ondestroy pour les socket ,
// gerer la remise a 0 du serveur
// bonus ajouter un utilisateur qui rentre en cours 


import com.example.project_master.R;
import com.example.project_master.model.Checkpoint;
import com.example.project_master.model.ObjectCoordonate;
import com.example.project_master.model.User;
import com.example.project_master.socket.Socket;
import com.example.project_master.socket.SocketCollectAllData;
import com.example.project_master.socket.SocketCollectCoordonate;
import com.example.project_master.socket.SocketRestartServer;
import com.example.project_master.socket.SocketSendCoordonate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private Bundle extra;
    private float minZoom=13f;
    private float maxZoom=20f;
    private User user;

    private ArrayList<User>listUser;
    private ArrayList<Checkpoint>checkpointCoordonate;
    private HashMap<User,Integer> teams;

    private Socket socketCollectCoordonate;
    private Socket socketCollectAllData;
    private Socket socketSendCoordonate;

    private HashMap<Checkpoint,Circle> mapCircle;
    private  HashMap<User,Marker> mapMarker;
    private HashMap<Checkpoint,Integer>wichTeamAsCheckpoint;
    private HashMap<Checkpoint,ArrayList<User>>wichUserAsCheckpoint;

    private User userToDraw;
    private boolean quitApp=false;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        extra=getIntent().getExtras();
        user=extra.getParcelable("user");

        mapCircle =new HashMap<Checkpoint,Circle>();
        checkpointCoordonate=new ArrayList<>();
        teams=new HashMap<>();
        listUser= new ArrayList<>();


        socketCollectAllData= new SocketCollectAllData(user,this);
        socketCollectAllData.initSocket();
        socketCollectAllData.receiveSocket();
        socketCollectAllData.emitSocket();

        wichTeamAsCheckpoint=new HashMap<>();
        wichUserAsCheckpoint=new HashMap<>();

        socketSendCoordonate=new SocketSendCoordonate(user,this);






    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    public void centreMapOnLocation(Location location){


        user.setUserLocation(new ObjectCoordonate(location.getLatitude(),location.getLongitude()));
        if (!quitApp){
            socketSendCoordonate.initSocket();
            socketSendCoordonate.receiveSocket();
            socketSendCoordonate.emitSocket();

            checkIfUserOnCheckpoint(user);
            userLeftCheckpoint(user);

        }






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                List<String> providers = locationManager.getProviders( true );
                Location bestLocation = null;
                for( String provider : providers ){
                    Location l = locationManager.getLastKnownLocation( provider );
                    if( l == null ){
                        continue;
                    }
                    if( bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy() ){
                        bestLocation = l; // Found best last known location;
                    }
                }
                CameraUpdate center= CameraUpdateFactory.newLatLngZoom(new LatLng(bestLocation.getLatitude(),bestLocation.getLongitude()),minZoom);
                mMap.moveCamera(center);

                centreMapOnLocation(bestLocation);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        socketCollectCoordonate= new SocketCollectCoordonate(user,this,googleMap);
        socketCollectCoordonate.initSocket();
        socketCollectCoordonate.receiveSocket();



        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(minZoom);
        mMap.setMaxZoomPreference(maxZoom);

        putCheckpointOnMap(mMap);
        mapMarker =createMapMarker(mMap);

        // demande la permussion à l'utilisateur



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                centreMapOnLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            List<String> providers = locationManager.getProviders( true );
            Location bestLocation = null;
            for( String provider : providers ){
                Location l = locationManager.getLastKnownLocation( provider );
                if( l == null ){
                    continue;
                }
                if( bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy() ){
                    bestLocation = l; // Found best last known location;

                }
            }

            System.out.println("2eme ssais"+ bestLocation);
            CameraUpdate center= CameraUpdateFactory.newLatLngZoom(new LatLng(bestLocation.getLatitude(),bestLocation.getLongitude()),minZoom);
            mMap.moveCamera(center);

            centreMapOnLocation(bestLocation);
        }
        else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},1);
        }

    }

    /**
     *
     * @param map
     * @param userToSet
     */
    public void setUserOnMap(GoogleMap map,User userToSet) {
        userToDraw=setUserCoordonate(userToSet);
        checkIfUserOnCheckpoint(userToDraw);
        userLeftCheckpoint(userToDraw);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapMarker.get(userToDraw).setPosition(new LatLng(userToDraw.getUserLocation().getLatitude(),userToDraw.getUserLocation().getLongitude()));
            }
        });

    }

    /**
     *
     * @param userToSet
     * @return
     */
    public User setUserCoordonate(User userToSet){

        for (User user: listUser){
            if (user.equals(userToSet)){
                user.setUserLocation(userToSet.getUserLocation());
                return user;
            }
        }
        return null;

    }

    /**
     * check witch color must be set on the checkpoint
     * @param userLeft
     */


    private void userLeftCheckpoint(User userLeft) {
        for (Map.Entry<Checkpoint,ArrayList<User>>map : wichUserAsCheckpoint.entrySet()){
            ArrayList<User>value= map.getValue();
            Checkpoint key= map.getKey();
            if (map.getValue().contains(userLeft)){
                ObjectCoordonate userCoodonate= userLeft.getUserLocation();
                ObjectCoordonate checkpointCoordonate= key.getCoordonate();
                float[] distance=new  float[1];
                Location.distanceBetween(userCoodonate.getLatitude(),userCoodonate.getLongitude(),checkpointCoordonate.getLatitude(),checkpointCoordonate.getLongitude(),distance);
                if (distance[0]>mapCircle.get(key).getRadius()){
                    value.remove(userLeft);
                    checkpointNeedChange(key,value);
                    if (value.size()==0){
                        setCheckpointColor(Color.TRANSPARENT,mapCircle.get(key));
                    }
                }
            }
        }

    }



    /**
     *
     * @param userToCheck
     */

    private void checkIfUserOnCheckpoint(final User userToCheck) {
        ObjectCoordonate userCoodonate= userToCheck.getUserLocation();
        for (final Checkpoint checkpoint : checkpointCoordonate){
            System.out.println(checkpointCoordonate);
            ObjectCoordonate checkpointCoordonate= checkpoint.getCoordonate();
            final float[] distance=new  float[1];
            Location.distanceBetween(userCoodonate.getLatitude(),userCoodonate.getLongitude(),checkpointCoordonate.getLatitude(),checkpointCoordonate.getLongitude(),distance);
            System.out.println(mapCircle);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (distance[0]< mapCircle.get(checkpoint).getRadius()) {
                        checkCheckpointNeedChange(userToCheck,checkpoint);
                    }
                }
            });

        }


    }


    /**
     *  set the color of the checkpoint
     * @param checkpoint
     * @param usersOnCheckpoint
     */
    private void checkpointNeedChange(Checkpoint checkpoint,ArrayList<User>usersOnCheckpoint){
        int number=usersOnCheckpoint.size();
        int numberAlly = 0;
        for (User user1 : usersOnCheckpoint) {
            if (teams.get(user1) == teams.get(this.user)) {
                numberAlly++;
            }
        }
        Circle checkpointCircle = mapCircle.get(checkpoint);
        if (checkpointCircle.getFillColor() == Color.TRANSPARENT) {
            if (numberAlly < number / 2) {
                setCheckpointColor(Color.RED, checkpointCircle);

            } else if (numberAlly == number / 2) {
                setCheckpointColor(Color.rgb(255, 165, 0), checkpointCircle);
            } else {
                setCheckpointColor(Color.RED, checkpointCircle);

            }
        } else {
            if (checkpointCircle.getFillColor() == Color.RED && numberAlly > number / 2) {
                setCheckpointColor(Color.BLUE, checkpointCircle);
            } else if (checkpointCircle.getFillColor() == Color.BLUE && numberAlly < number / 2) {
                setCheckpointColor(Color.RED, checkpointCircle);
            } else if (checkpointCircle.getFillColor() == Color.rgb(255, 165, 0) && numberAlly > number / 2) {
                setCheckpointColor(Color.BLUE, checkpointCircle);
            } else if (checkpointCircle.getFillColor() == Color.rgb(255, 165, 0) && numberAlly < number / 2) {
                setCheckpointColor(Color.RED, checkpointCircle);
            }
        }

    }

    private void checkCheckpointNeedChange(User user,Checkpoint checkpoint) {
        ArrayList<User>usersOnCheckpoint= wichUserAsCheckpoint.get(checkpoint);
        if (!usersOnCheckpoint.contains(user)){
            usersOnCheckpoint.add(user);

            int number=usersOnCheckpoint.size();

            if (number==1) {
                wichTeamAsCheckpoint.replace(checkpoint,teams.get(user));
                if (teams.get(user)==teams.get(this.user)){
                    mapCircle.get(checkpoint).setFillColor(Color.BLUE);

                }
                else {
                    mapCircle.get(checkpoint).setFillColor(Color.RED);

                }
            }
            else {
                checkpointNeedChange(checkpoint,usersOnCheckpoint);

            }

        }
    }

    /**
     *
     * @param color
     * @param circle
     */
    private void setCheckpointColor(final int color, final Circle circle){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circle.setFillColor(color);

            }
        });
    }


    /**
     *
     * @param map
     */
    private void putCheckpointOnMap(GoogleMap map) {

        for (Checkpoint checkpoint : checkpointCoordonate) {

            // initialize wichUserAsCheckpoint and wichTeamAsChekpoint
            wichTeamAsCheckpoint.put(checkpoint,-1);
            wichUserAsCheckpoint.put(checkpoint,new ArrayList<User>());

            // create the checkpoint area
            CircleOptions circleOptions= new CircleOptions()
                    .center(new LatLng(checkpoint.getCoordonate().getLatitude(),checkpoint.getCoordonate().getLongitude()))
                    .radius(20)
                    .strokeWidth(10)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.TRANSPARENT);

            Circle circle= mMap.addCircle(circleOptions);
            mapCircle.put(checkpoint,circle);

        }

    }

    /**
     * initialize for each user a marker
     * @param map
     * @return
     */
    private  HashMap<User, Marker> createMapMarker(GoogleMap map){
        HashMap<User,Marker> mapMarkerOption= new HashMap<>();
        for (User user: listUser){
            if (!user.equals(this.user)) {
                MarkerOptions markerOptions= new MarkerOptions()
                        .title(user.getUsername())
                        .position(new LatLng(0,0));
                if (teams.get(user)==teams.get(this.user)){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                else{markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); }
                Marker marker=map.addMarker(markerOptions);
                mapMarkerOption.put(user,marker);

            }

        }
        return mapMarkerOption;

    }


    public void drawOnMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * send socket to server to restart the server
     * @param view
     */
    public void restartServer(View view) {
        final  Context context=this;
        final AlertDialog.Builder confirmation= new AlertDialog.Builder(this);
        confirmation.setMessage(R.string.endApp);
        confirmation.setPositiveButton(R.string.removeYes,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quitApp=true;
                if (user.equals(listUser.get(0))){
                    SocketRestartServer socketRestartServer=new SocketRestartServer(user,context);
                    socketRestartServer.initSocket();
                    socketRestartServer.receiveSocket();
                    socketRestartServer.emitSocket();
                }
                socketSendCoordonate.destroySocket();
                socketCollectCoordonate.destroySocket();
                socketCollectAllData.destroySocket();
                finish();

            }
        });
        confirmation.setNegativeButton(R.string.removeNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmation.show();

    }



    public ArrayList<User> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<User> listUser) {
        this.listUser = listUser;
    }

    public ArrayList<Checkpoint> getCheckpointCoordonate() {
        return checkpointCoordonate;
    }

    public void setCheckpointCoordonate(ArrayList<Checkpoint> checkpointCoordonate) {
        this.checkpointCoordonate = checkpointCoordonate;
    }

    public HashMap<User, Integer> getTeams() {
        return teams;
    }

    public void setTeams(HashMap<User, Integer> teams) {
        this.teams = teams;
    }


}