package com.thesis.bmm.smartplug.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.activities.GraphicActivity;
import com.thesis.bmm.smartplug.model.Plugs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MUHAMMED on 23.01.2018.
 */

public class RecyclerPlugListAdapter extends RecyclerView.Adapter<RecyclerPlugListViewHolder> {
    List<Plugs> plugsList;
    Context context;

    public RecyclerPlugListAdapter(ArrayList<Plugs> plugsList, Context context) {
        this.plugsList = plugsList;
        this.context = context;
    }


    @Override
    public RecyclerPlugListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pluglist_item, parent, false);
        return new RecyclerPlugListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerPlugListViewHolder holder, final int position) {
        holder.txtRoomName.setText(plugsList.get(position).getPlugRoom());
        holder.txtPlugName.setText(plugsList.get(position).getPlugName());
        holder.plugStatus.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Plugs").child(plugsList.get(position).getPlugID());
                        if (isChecked) {
                            Plugs plugs = new Plugs(plugsList.get(position).getPlugID(), plugsList.get(position).getPlugName(), plugsList.get(position).getPlugRoom(), plugsList.get(position).getPlugCurrent(), true);
                            dr.setValue(plugs);
                        } else {
                            Plugs plugs = new Plugs(plugsList.get(position).getPlugID(), plugsList.get(position).getPlugName(), plugsList.get(position).getPlugRoom(), plugsList.get(position).getPlugCurrent(), false);
                            dr.setValue(plugs);
                        }
                    }
                });
        holder.currentGraphicInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GraphicActivity.class);
                intent.putExtra("plugID", plugsList.get(position).getPlugID());
                context.startActivity(intent);
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(""+context.getResources().getString(R.string.plugupdate));
                alertDialog.setMessage(""+context.getResources().getString(R.string.plugupdateexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Priz güncelleme kodu
                            }
                        });
                alertDialog.setNegativeButton(""+context.getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(""+context.getResources().getString(R.string.plugdelete));
                alertDialog.setMessage(""+context.getResources().getString(R.string.plugdeleteexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Priz silme kodu
                            }
                        });
                alertDialog.setNegativeButton(""+context.getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return plugsList.size();
    }
}
