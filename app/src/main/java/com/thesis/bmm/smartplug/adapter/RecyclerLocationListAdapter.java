package com.thesis.bmm.smartplug.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.thesis.bmm.smartplug.LocationRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Locations;
import com.thesis.bmm.smartplug.services.NotificationReceiver;

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

    @Override
    public RecyclerLocationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.locationslist_item, parent, false);
        return new RecyclerLocationListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerLocationListViewHolder holder, final int position) {
        final Locations location = locationsList.get(position);
        holder.adress.setText(location.getProvince() + " " + location.getDistrict() + " " + location.getRegion());
        holder.notification_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDailyInterruptRequest(location.getProvince().toString(), location.getDistrict().toString(), location.getRegion().toString().substring(0, location.getRegion().toString().length() - 9));
                } else {
                    cancelDailyInterruptRequest();
                }
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationRequest locationRequest = new LocationRequest(context);
                locationRequest.selectAdressDialog(0, locationsList.get(position).getLocationID());

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationRequest locationRequest = new LocationRequest(context);
                locationRequest.deleteLocation(locationsList.get(position).getLocationID());
            }
        });
    }

    private void setDailyInterruptRequest(String province, String district, String region) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("Province", province);
        intent.putExtra("District", district);
        intent.putExtra("Region", region);
        pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
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
