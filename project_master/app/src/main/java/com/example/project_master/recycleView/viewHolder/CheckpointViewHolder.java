package com.example.project_master.recycleView.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_master.model.Checkpoint;
import com.example.project_master.R;

public class CheckpointViewHolder extends RecyclerView.ViewHolder {

        TextView etablishementName;
        TextView etablishementlatitude;
        TextView etablishementlongitude;


        public CheckpointViewHolder(@NonNull View itemView) {
            super(itemView);
            etablishementlatitude = itemView.findViewById(R.id.etablishementlatitude);
            etablishementlongitude = itemView.findViewById(R.id.etablishementlongitude);
            etablishementName = itemView.findViewById(R.id.etablishementName);

        }



    public void bind (Checkpoint checkpoint) {
            etablishementName.setText(checkpoint.getName());
            etablishementlatitude.setText("latitude: "+Double.toString(checkpoint.getCoordonate().getLatitude()));
            etablishementlongitude.setText("longitude: "+Double.toString(checkpoint.getCoordonate().getLongitude()));


        }

}


