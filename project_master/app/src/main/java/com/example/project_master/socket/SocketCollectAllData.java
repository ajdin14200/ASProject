package com.example.project_master.socket;

import android.content.Context;

import com.example.project_master.model.Checkpoint;
import com.example.project_master.activity.MapsActivity;
import com.example.project_master.model.ObjectCoordonate;
import com.example.project_master.SplitResponse;
import com.example.project_master.model.User;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SocketCollectAllData extends Socket {


    private ArrayList<Checkpoint> checkpointCoordonate;
    private  ArrayList<User> listUser;
    private  HashMap<User,Integer> teams;
    public SocketCollectAllData(User user, Context context) {
        super(user, context);
    }


    /**
     * need to collect all data before drawing the map
     */
    public void checkToSend(){
        if (checkpointCoordonate!=null && listUser!=null && teams!=null) {

            ((MapsActivity)context).setCheckpointCoordonate(checkpointCoordonate);
            ((MapsActivity)context).setListUser(listUser);
            ((MapsActivity)context).setTeams(teams);
            ((MapsActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MapsActivity)context).drawOnMap();
                }
            });

        }
    }

    @Override
    public void emitSocket() {
        while (!socket.connected()){}
        socket.emit("collectAll","");

    }

    @Override
    public void receiveSocket() {
        socket.on("checkpoints", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String res= (String) args[0];
                SplitResponse splitResponse= new SplitResponse();
                ArrayList<ArrayList<String>> data=splitResponse.splitDoubleListData(res);
                checkpointCoordonate= new ArrayList<>();
                for (ArrayList<String> checkpointData : data){
                    ObjectCoordonate objectCoordonate= new ObjectCoordonate(Double.parseDouble(checkpointData.get(1)),Double.parseDouble(checkpointData.get(2)));
                    Checkpoint checkpoint= new Checkpoint(checkpointData.get(0),objectCoordonate);
                    checkpointCoordonate.add(checkpoint);
                }
                checkToSend();

            }
        });

        socket.on("players", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONArray data= (JSONArray)args[0];
                listUser= new ArrayList<>();
                for (int i=0; i<data.length();i++){
                    try {
                        String player= String.valueOf(data.get(i));
                        if (player.equals(user.getUsername())) {listUser.add(user);}
                        else {listUser.add(new User(player,null));}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                checkToSend();
            }
        });

        socket.on("teams", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONArray data= (JSONArray)args[0];
                teams= new HashMap<>();
                int teamnumber=0;
                for (int i=0;i<data.length();i++){
                    try {
                        JSONArray array=(JSONArray)data.get(i);
                        for (int j=0;j<array.length();j++) {
                            String player= null;
                            try {
                                player = String.valueOf(array.get(j));
                                if (player.equals(user.getUsername())){
                                    teams.put(user,teamnumber);
                                }
                                else {teams.put(new User(player,null),teamnumber);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            teamnumber++;
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                checkToSend();

            }
        });

    }
}
