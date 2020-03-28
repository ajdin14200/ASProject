package com.example.project_master.socket;

import android.content.Context;

import com.example.project_master.model.User;
import com.github.nkzawa.emitter.Emitter;

public class SocketSendCoordonate extends Socket {


    public SocketSendCoordonate(User user, Context context) {
        super(user, context);
    }


    @Override
    public void emitSocket() {
        while (!socket.connected()){}
        socket.emit("coordonnee",user.sendInformation());

    }

    @Override
    public void receiveSocket() {
        socket.on("coord", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                destroySocket();

            }
        });
    }
}


