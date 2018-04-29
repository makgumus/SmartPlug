package com.thesis.bmm.smartplug.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.LocationRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Locations;
import com.thesis.bmm.smartplug.services.InterruptNotificationReceiver;

import java.util.Calendar;
import java.util.List;

/**
 * Created by MUHAMMED on 13.03.2018.
 */

public class RecyclerLocationListAdapter extends RecyclerView.Adapter<RecyclerLocationListViewHolder> {
    List<Locations> locationsList;
    Context context;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public RecyclerLocationListAdapter(List<Locations> locationsList, Context context) {
        this.locationsList = locationsList;
        this.context = context;
    }

    @Override
    public RecyclerLocationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.locationslist_item, parent, false);
        return new RecyclerLocationListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerLocationListViewHolder holder, final int position) {
        final Locations location = locationsList.get(position);
        final LocationRequest locationRequest = new LocationRequest(context);
        final InterruptRequest interruptRequest = new InterruptRequest(context, location.getProvince().toString(), location.getDistrict().toString(), location.getRegion().toString());
        holder.adress.setText(location.getProvince() + "\n" + location.getDistrict() + "\n" + location.getRegion());
        holder.notification_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationRequest.updateLocationatFirebase(location.getLocationID().toString(), location.getProvince().toString(), location.getDistrict().toString(), location.getRegion().toString(), true);
                    interruptRequest.request(location.getLocationID().toString());
                    setDailyInterruptRequest(location.getProvince().toString(), location.getDistrict().toString(), location.getRegion().toString(), location.getLocationID().toString());
                } else {
                    locationRequest.updateLocationatFirebase(location.getLocationID(), location.getProvince(), location.getDistrict(), location.getRegion(), false);
                    interruptRequest.deleteInterruptatFirebase(location.getLocationID().toString());
                    cancelDailyInterruptRequest();
                }
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(""+context.getResources().getString(R.string.addressupdate));
                alertDialog.setMessage(""+context.getResources().getString(R.string.addressupdateexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LocationRequest locationRequest = new LocationRequest(context);
                                locationRequest.selectAdressDialog(0, location.getLocationID());
                                interruptRequest.deleteInterruptatFirebase(location.getLocationID());
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
                alertDialog.setTitle(""+context.getResources().getString(R.string.addressdelete));
                alertDialog.setMessage(""+context.getResources().getString(R.string.addressdeleteexample));
                alertDialog.setIcon(R.drawable.smartplug);
                alertDialog.setPositiveButton(""+context.getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LocationRequest locationRequest = new LocationRequest(context);
                                locationRequest.deleteLocation(location.getLocationID());
                                interruptRequest.deleteInterruptatFirebase(location.getLocationID());
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
        holder.notification_status.setChecked(location.getNotificationStatus());
    }

    private void setDailyInterruptRequest(String province, String district, String region, String locationId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Intent intent = new Intent(context, InterruptNotificationReceiver.class);
        intent.putExtra("LocationID", locationId);
        intent.putExtra("Province", province);
        intent.putExtra("District", district);
        intent.putExtra("Region", region);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelDailyInterruptRequest() {
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }
}
