package com.thesis.bmm.smartplug.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.activities.MainActivity;
import com.thesis.bmm.smartplug.model.Interrupts;


/**
 * Created by MUHAMMED on 12.03.2018.
 */

public class InterruptNotificationReceiver extends BroadcastReceiver {
    DatabaseReference databaseReference;

    @Override
    public void onReceive(Context context, Intent intent) {
        String locationID = intent.getStringExtra("LocationID");
        String province = intent.getStringExtra("Province");
        String district = intent.getStringExtra("District");
        String region = intent.getStringExtra("Region");
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        databaseReference = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Interrupts");
        databaseReference.child(locationID).removeValue();
        InterruptRequest interruptRequest = new InterruptRequest(context, province, district, region);
        interruptRequest.request(locationID);
        getData(context, firebaseUserInformation.getFirebaseUserId());
    }

    private void getData(final Context cntxt, String userId) {
        final InterruptRequest interruptRequest = new InterruptRequest();
        databaseReference = FirebaseDatabase.getInstance().getReference("" + userId).child("Interrupts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Interrupts interrupt = postSnapshot.getValue(Interrupts.class);
                    if (Build.VERSION.SDK_INT > 15 && interrupt.getExplain() != null) {
                        NotificationManager notificationManager = (NotificationManager) cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intentFragment = new Intent(cntxt, MainActivity.class);
                        intentFragment.putExtra("Pager", "1");
                        PendingIntent pendingIntent = PendingIntent.getActivity(cntxt, 100, intentFragment, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(cntxt, "default")
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle(interruptRequest.dateTimeNowToString() + "" + cntxt.getResources().getString(R.string.interruption))
                                .setContentText(interrupt.getExplain())
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
