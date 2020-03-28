package com.example.project_master.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.project_master.R;
import com.example.project_master.model.Checkpoint;
import com.example.project_master.model.ObjectCoordonate;
import com.example.project_master.model.User;
import com.example.project_master.recycleView.adaptater.CheckpointAdaptater;
import com.example.project_master.socket.Socket;
import com.example.project_master.socket.SocketSendGameReady;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCheckPoint extends AppCompatActivity {

    AutocompleteSupportFragment autocompleteFragment;
    PlacesClient placesClient;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    public ArrayList<Checkpoint> checkpointCoordonate;
    private RecyclerView checkpointRecycler;
    private CheckpointAdaptater checkpointAdaptater;
    private  Intent searchPlace;
    private Bundle extra;
    private User user;
    private Socket socketSendGameReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_check_point);
        checkpointCoordonate= new ArrayList();
        checkpointRecycler= findViewById(R.id.recyclerCheckpoint);
        checkpointRecycler.setLayoutManager(new LinearLayoutManager(this));
        checkpointAdaptater= new CheckpointAdaptater(this,checkpointCoordonate);
        checkpointRecycler.setAdapter(checkpointAdaptater);

        extra=getIntent().getExtras();
        user=extra.getParcelable("user");

        socketSendGameReady= new SocketSendGameReady(user,this,checkpointCoordonate);
        socketSendGameReady.initSocket();


        findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);

        placesClient = Places.createClient(this);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);
        searchPlace = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(searchPlace, AUTOCOMPLETE_REQUEST_CODE);



    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * check if user give all permission require
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                Checkpoint checkpoint= new Checkpoint(place.getName(),new ObjectCoordonate(place.getLatLng().latitude,place.getLatLng().longitude));
                checkpointCoordonate.add(checkpoint);
                checkpointAdaptater.notifyDataSetChanged();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * start the Places api to get checkpoint
     * @param view
     */
    public void addCheckpoint(View view) {
        // Create a new Places client instance.
        startActivityForResult(searchPlace, AUTOCOMPLETE_REQUEST_CODE);


    }

    public void startGame(View view){
        if (checkpointCoordonate.size()>0) {

            socketSendGameReady.emitSocket();


        }
        else{
            Snackbar.make(findViewById(R.id.snackbar),R.string.nocheckpoint,2000);
        }

    }

     @Override
     public void onDestroy() {

         super.onDestroy();
         //socketSendGameReady.destroySocket();

     }


}
