package com.example.project_master.socket;

import android.content.Context;

import com.example.project_master.activity.MapsActivity;
import com.example.project_master.model.ObjectCoordonate;
import com.example.project_master.SplitResponse;
import com.example.project_master.model.User;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class SocketCollectCoordonate extends Socket {

    private GoogleMap map;
    public SocketCollectCoordonate(User user, Context context, GoogleMap map) {
        super(user, context);
        this.map=map;
    }


    @Override
    public void receiveSocket() {
        socket.on("coord", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //JSONObject data= (JSONObject)args[0];
                String res= String.valueOf(args[0]);
                System.out.println("res");
                SplitResponse splitResponse= new SplitResponse();
                ArrayList<String>data=splitResponse.splitSingleListData(res);
                User userToSet=new User(data.get(0),new ObjectCoordonate(Double.parseDouble(data.get(1)),Double.parseDouble(data.get(2))));
                boolean b=user.equals(userToSet);
                if (!user.equals(userToSet)){
                    ((MapsActivity)context).setUserOnMap(map,userToSet);
                }

            }
        });
    }
}
