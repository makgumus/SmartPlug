package com.thesis.bmm.smartplug.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.EditPlugDialog;
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String  datauserid= sharedPreferences.getString("userID", "Yok") ;
        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference(""+datauserid).child("Plugs").child(plugsList.get(position).getPlugID());
        final Plugs plug = plugsList.get(position);
        holder.txtRoomName.setText(plug.getPlugRoom());
        holder.txtPlugName.setText(plug.getPlugName());
        holder.plugStatus.setChecked(plug.getPlugStatus());
        holder.plugStatus.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                        if (isChecked) {
                            Plugs plugs = new Plugs(plug.getPlugID(), plug.getPlugName(), plug.getPlugRoom(), plug.getPlugCurrent(), true);
                            dr.setValue(plugs);
                        } else {
                            Plugs plugs = new Plugs(plug.getPlugID(), plug.getPlugName(), plug.getPlugRoom(), plug.getPlugCurrent(), false);
                            dr.setValue(plugs);
                        }
                    }
                });
        holder.currentGraphicInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GraphicActivity.class);
                intent.putExtra("plugID", plug.getPlugID());
                context.startActivity(intent);
            }
        });
        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(""+context.getResources().getString(R.string.plugupdate));
                alertDialog.setMessage(""+context.getResources().getString(R.string.plugupdateexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditPlugDialog plugDialog = new EditPlugDialog(context);
                                plugDialog.selectPlugDialog(0, plug.getPlugID());
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
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(""+context.getResources().getString(R.string.plugdelete));
                alertDialog.setMessage(""+context.getResources().getString(R.string.plugdeleteexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditPlugDialog plugDialog = new EditPlugDialog(context);
                                plugDialog.deletePlugatFirebase(plug.getPlugID());
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
