package com.example.project_master.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.project_master.R;
import com.example.project_master.model.User;
import com.example.project_master.socket.Socket;
import com.example.project_master.socket.SocketConnection;
import com.google.android.material.snackbar.Snackbar;

public class CreateUser extends AppCompatActivity {

    private EditText username;
    private User user;
    private boolean oneTime= false;
    private Socket socketConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        username= (EditText)findViewById(R.id.username);
        socketConnection= new SocketConnection(user,this);
        socketConnection.initSocket();


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                sendNewUser(username.getText().toString());
            }
        }
    }
    public void sendNewUser(String text){

        oneTime=true;
        System.out.println(username.getText());
        user= new User(text,null);

       socketConnection.user=this.user;
        socketConnection.receiveSocket();
        socketConnection.emitSocket();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketConnection.destroySocket();


    }

    public void createUser(View view) {
        if (!oneTime){
            String text=username.getText().toString();
            if (text.equals("")){
                Snackbar.make(findViewById(R.id.snackbar),R.string.emptyuser,2000).show();
            }
            else{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                    sendNewUser(text);
                }
                else {

                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},1);
                }



            }



        }

    }
}
