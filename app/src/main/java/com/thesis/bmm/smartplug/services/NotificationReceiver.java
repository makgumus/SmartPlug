package com.thesis.bmm.smartplug.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.activities.MainActivity;


/**
 * Created by MUHAMMED on 12.03.2018.
 */

public class NotificationReceiver extends BroadcastReceiver {
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences ;
    String  datauserid ;

    @Override
    public void onReceive(Context context, Intent intent) {
        String locationID = intent.getStringExtra("LocationID");
        String province = intent.getStringExtra("Province");
        String district = intent.getStringExtra("District");
        String region = intent.getStringExtra("Region");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        datauserid= sharedPreferences.getString("userID", "Yok") ;
        databaseReference = FirebaseDatabase.getInstance().getReference(""+datauserid).child("Interrupts");
        databaseReference.child(locationID).removeValue();
        InterruptRequest interruptRequest = new InterruptRequest(context, province, district, region);
        interruptRequest.request(locationID);
        getData(context);
    }

    private void getData(final Context cntxt) {
        final InterruptRequest interrupt = new InterruptRequest();
        databaseReference = FirebaseDatabase.getInstance().getReference(""+datauserid).child("Interrupts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String interruptExplain = postSnapshot.getValue().toString();
                    if (Build.VERSION.SDK_INT > 15 && interruptExplain != null) {
                        NotificationManager notificationManager = (NotificationManager) cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intentFragment = new Intent(cntxt, MainActivity.class);
                        intentFragment.putExtra("Pager", "1");
                        PendingIntent pendingIntent = PendingIntent.getActivity(cntxt, 100, intentFragment, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(cntxt, "default")
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle(interrupt.dateTimeNowToString() + ""+cntxt.getResources().getString(R.string.interruption))
                                .setContentText(interruptExplain)
                                .setAutoCancel(true);
                        notificationManager.notify(1, notification.build());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
