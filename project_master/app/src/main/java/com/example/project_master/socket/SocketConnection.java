package com.example.project_master.socket;

import android.content.Context;
import android.content.Intent;

import com.example.project_master.activity.CreateCheckPoint;
import com.example.project_master.activity.CreateUser;
import com.example.project_master.activity.MapsActivity;
import com.example.project_master.R;
import com.example.project_master.model.User;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.snackbar.Snackbar;


public class SocketConnection extends Socket {


    public SocketConnection(User user, Context context)  {
        super(user, context);
    }




    @Override
    public void emitSocket() {
        while (!socket.connected()){}
        socket.emit("firstuser",user.getUsername());

    }

    @Override
    public void receiveSocket() {
        socket.on("first", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data= (String) args[0];
                if (user.getUsername().equals(data)) {
                    Intent createCheckpoint = new Intent(context, CreateCheckPoint.class);
                    createCheckpoint.putExtra("user",user);
                    destroySocket();
                    context.startActivity(createCheckpoint);
                    ((CreateUser)context).finish();
                }

            }
        });
        socket.on("otherwait", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(((CreateUser)context).findViewById(R.id.snackbar),R.string.waitgamemaster,2000);
            }
        });

        socket.on("start", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(!args[0].equals(user.getUsername())){
                    Intent mapActivity = new Intent(context, MapsActivity.class);
                    mapActivity.putExtra("user",user);
                    destroySocket();
                    context.startActivity(mapActivity);
                    ((CreateUser)context).finish();
                }

            }
        });
    }
}
