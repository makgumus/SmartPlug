package com.thesis.bmm.smartplug.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.activities.MainActivity;


/**
 * Created by MUHAMMED on 12.03.2018.
 */

public class NotificationReceiver extends BroadcastReceiver {
    ListView listView;

    @Override
    public void onReceive(Context context, Intent intent) {
        String province = intent.getStringExtra("Province");
        String district = intent.getStringExtra("District");
        String region = intent.getStringExtra("Region");
        InterruptRequest interruptRequest = new InterruptRequest(context, province, district, region);
        interruptRequest.request();
        if (interruptRequest.getInterruptSize() != 0) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intentFragment = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intentFragment, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder notification = new Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.support.v4.R.drawable.notification_bg_low_normal)
                    .setContentTitle(interruptRequest.dateTimeNowToString() + " Tarihinde elektrik kesintisi var")
                    .setContentText(interruptRequest.electricityInterruptList.get(0).getExplain())
                    .setAutoCancel(true);
        }
    }
}
