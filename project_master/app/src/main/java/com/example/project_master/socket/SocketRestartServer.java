package com.example.project_master.socket;

import android.app.Activity;
import android.content.Context;

import com.example.project_master.model.User;
import com.github.nkzawa.emitter.Emitter;

public class SocketRestartServer extends Socket {
    public SocketRestartServer(User user, Context context) {
        super(user, context);
    }



    @Override
    public void emitSocket() {
        while (!socket.connected()){}
        socket.emit("restartGame","restart");


    }

}
