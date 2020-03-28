package com.example.project_master.socket;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.project_master.model.User;
import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;

public class Socket{


    public User user;
    public Context context;
    protected String url1= "http://ec2-3-10-56-65.eu-west-2.compute.amazonaws.com:8080";
    protected int port= 8080;
    protected com.github.nkzawa.socketio.client.Socket socket;


    public Socket(User user,Context context)  {
        this.user=user;
        this.context=context;
        try {
            socket= IO.socket(url1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }



    public void initSocket()
    {
        socket.connect();

    }

    public void emitSocket(){

    }

    public void receiveSocket(){

    }

    public void destroySocket(){

        socket.close();
    }



}
