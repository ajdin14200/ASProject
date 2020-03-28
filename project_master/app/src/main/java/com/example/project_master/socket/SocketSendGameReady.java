package com.example.project_master.socket;

import android.content.Context;
import android.content.Intent;

import com.example.project_master.model.Checkpoint;
import com.example.project_master.activity.CreateCheckPoint;
import com.example.project_master.activity.MapsActivity;
import com.example.project_master.model.User;

import java.util.ArrayList;

public class SocketSendGameReady extends Socket {

    private ArrayList<Checkpoint>checkpointCoordonate;
    public SocketSendGameReady(User user, Context context, ArrayList<Checkpoint>listCheckpoint) {
        super(user, context);
        this.checkpointCoordonate =listCheckpoint;
    }




    @Override
    public void emitSocket() {
        ArrayList arraytosend= new ArrayList();
        for (Checkpoint checkpoint : checkpointCoordonate) {
            arraytosend.add(checkpoint.sendInformation());
        }
        socket.emit("gamestart",arraytosend);
        Intent mapActivity= new Intent(context, MapsActivity.class);
        mapActivity.putExtra("user",user);
        context.startActivity(mapActivity);
        ((CreateCheckPoint)context).finish();
    }
}


