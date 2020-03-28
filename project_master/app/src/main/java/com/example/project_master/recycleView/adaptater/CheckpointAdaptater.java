package com.example.project_master.recycleView.adaptater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_master.model.Checkpoint;
import com.example.project_master.R;
import com.example.project_master.recycleView.viewHolder.CheckpointViewHolder;

import java.util.ArrayList;

public class CheckpointAdaptater extends RecyclerView.Adapter<CheckpointViewHolder> {

    Context context;
    ArrayList<Checkpoint> arrayList;
    Checkpoint checkpoint;


    public CheckpointAdaptater(Context context, ArrayList<Checkpoint> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CheckpointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_checkpoint, parent, false);
        return new CheckpointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckpointViewHolder holder, final int position) {

        checkpoint=arrayList.get(position);
        holder.bind(checkpoint);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder confirmation= new AlertDialog.Builder(context);
                confirmation.setMessage(R.string.messagecheckpoint);
                confirmation.setPositiveButton(R.string.removeYes,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.remove(checkpoint);
                        notifyDataSetChanged();
                    }
                });
                confirmation.setNegativeButton(R.string.removeNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirmation.show();

                return false;
            }
        });




    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



}
