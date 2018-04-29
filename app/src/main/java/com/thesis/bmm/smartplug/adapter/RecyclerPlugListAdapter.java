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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.EditPlugDialog;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.activities.GraphicActivity;
import com.thesis.bmm.smartplug.model.Interrupts;
import com.thesis.bmm.smartplug.model.Locations;
import com.thesis.bmm.smartplug.model.Plugs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by MUHAMMED on 23.01.2018.
 */

public class RecyclerPlugListAdapter extends RecyclerView.Adapter<RecyclerPlugListViewHolder> {
    private List<Plugs> plugsList;
    private Context context;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private DatabaseReference drInterrupt, drLocation;

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
    public void onBindViewHolder(final RecyclerPlugListViewHolder holder, final int position) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs").child(plugsList.get(position).getPlugID());
        final Plugs plug = plugsList.get(position);
        holder.txtRoomName.setText(plug.getPlugRoom());
        holder.txtPlugName.setText(plug.getPlugName());
        holder.plugStatus.setChecked(plug.getPlugStatus());
        holder.plugStatus.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        if (isChecked) {
                            if (holder.txtPlugName.getText().equals("Çamaşır Makinesi") || holder.txtPlugName.getText().equals("Washing Machine") || holder.txtPlugName.getText().equals("Bulaşık Makinesi") || holder.txtPlugName.getText().equals("Dishwasher")) {
                                calendar = Calendar.getInstance();
                                sdf = new SimpleDateFormat("HH:mm");
                                String time = sdf.format(calendar.getTime());
                                getLocation(time, holder, dr, plug);
                            } else {
                                Plugs plugs = new Plugs(plug.getPlugID(), plug.getPlugName(), plug.getPlugRoom(), plug.getPlugCurrent(), true);
                                dr.setValue(plugs);
                            }
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
                alertDialog.setTitle("" + context.getResources().getString(R.string.plugupdate));
                alertDialog.setMessage("" + context.getResources().getString(R.string.plugupdateexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton("" + context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditPlugDialog plugDialog = new EditPlugDialog(context);
                                plugDialog.selectPlugDialog(0, plug.getPlugID());
                            }
                        });
                alertDialog.setNegativeButton("" + context.getResources().getString(R.string.no),
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
                alertDialog.setTitle("" + context.getResources().getString(R.string.plugdelete));
                alertDialog.setMessage("" + context.getResources().getString(R.string.plugdeleteexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton("" + context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditPlugDialog plugDialog = new EditPlugDialog(context);
                                plugDialog.deletePlugatFirebase(plug.getPlugID());
                            }
                        });
                alertDialog.setNegativeButton("" + context.getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void getLocation(final String time, final RecyclerPlugListViewHolder holder, final DatabaseReference databaseReference, final Plugs plug) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        drLocation = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Locations");
        drLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Locations location = dataSnapshot.getValue(Locations.class);
                getInterruptNotification(location.getLocationID(), time, holder, databaseReference, plug);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getInterruptNotification(String Id, final String calendar, final RecyclerPlugListViewHolder holder, final DatabaseReference databaseR, final Plugs plug) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        drInterrupt = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Interrupts");
        drInterrupt.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Interrupts interrupt = postSnapshot.getValue(Interrupts.class);
                    if (!interrupt.getStartingTime().equals("")) {
                        int hourNow = Integer.parseInt(calendar.split(":")[0]);
                        int minuteNow = Integer.parseInt(calendar.split(":")[1]);
                        int interruptHour = Integer.parseInt(interrupt.getStartingTime().split(":")[0]);
                        int interruptMinute = Integer.parseInt(interrupt.getStartingTime().split(":")[1]);
                        int hourOdds = interruptHour - hourNow;
                        int mineteOdds = interruptMinute - minuteNow;
                        if (mineteOdds == 0 && hourOdds == 1) {
                            getAlert(databaseR, plug, holder);
                        } else if (hourOdds == 1 && mineteOdds < 0) {
                            getAlert(databaseR, plug, holder);
                        } else {
                            Plugs plugs = new Plugs(plug.getPlugID(), plug.getPlugName(), plug.getPlugRoom(), plug.getPlugCurrent(), true);
                            databaseR.setValue(plugs);
                        }
                    } else {
                        Plugs plugs = new Plugs(plug.getPlugID(), plug.getPlugName(), plug.getPlugRoom(), plug.getPlugCurrent(), true);
                        databaseR.setValue(plugs);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAlert(final DatabaseReference dr, final Plugs plugss, final RecyclerPlugListViewHolder holders) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("" + context.getResources().getString(R.string.plugupdate));
        alertDialog.setMessage("" + context.getResources().getString(R.string.plugupdateexample));
        alertDialog.setIcon(R.drawable.smartplug);
        alertDialog.setPositiveButton("" + context.getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Plugs plugs = new Plugs(plugss.getPlugID(), plugss.getPlugName(), plugss.getPlugRoom(), plugss.getPlugCurrent(), true);
                        dr.setValue(plugs);
                    }
                });
        alertDialog.setNegativeButton("" + context.getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        holders.plugStatus.setChecked(false);
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return plugsList.size();
    }
}
