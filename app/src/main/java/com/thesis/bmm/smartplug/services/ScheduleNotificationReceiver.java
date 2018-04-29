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
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.activities.MainActivity;

import java.text.SimpleDateFormat;


public class ScheduleNotificationReceiver extends BroadcastReceiver {
    private DatabaseReference drUserSchedule, drPlugs, drPieChartData;
    private float T1 = 0, T2 = 0, T3 = 0;
    private SimpleDateFormat df = new SimpleDateFormat("MM");
    private String caution, needToSchedule, userSchedule;

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        getSchedule(firebaseUserInformation.getFirebaseUserId(), context);
    }

    private void getSchedule(String userID, final Context cntxt) {
        drUserSchedule = FirebaseDatabase.getInstance().getReference("" + userID);
        drUserSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals("userSchedule")) {
                        userSchedule = postSnapshot.getValue().toString();
                    }
                    if (postSnapshot.getKey().equals("totalT1")) {
                        String t1 = postSnapshot.getValue().toString();
                        T1 = Float.parseFloat(t1);
                    }
                    if (postSnapshot.getKey().equals("totalT2")) {
                        String t2 = postSnapshot.getValue().toString();
                        T2 = Float.parseFloat(t2);
                    }
                    if (postSnapshot.getKey().equals("totalT3")) {
                        String t3 = postSnapshot.getValue().toString();
                        T3 = Float.parseFloat(t3);
                    }
                }
                if (T1 > T2 && T1 > T3) {
                    needToSchedule = "Sabah";
                } else if (T2 > T1 && T2 > T3) {
                    needToSchedule = "Puant";
                } else if (T3 > T1 && T3 > T2) {
                    needToSchedule = "Gece";
                } else {
                    needToSchedule = "";
                }
                if (!userSchedule.equals(needToSchedule) && !needToSchedule.equals("")) {
                    if (Build.VERSION.SDK_INT > 15) {
                        NotificationManager notificationManager = (NotificationManager) cntxt.getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intentFragment = new Intent(cntxt, MainActivity.class);
                        intentFragment.putExtra("Pager", "1");
                        PendingIntent pendingIntent = PendingIntent.getActivity(cntxt, 100, intentFragment, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(cntxt, "default")
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("Elektrik tarife kullanım uyarısı!")
                                .setContentText(getNeedToSchedule(needToSchedule))
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

    private String getNeedToSchedule(String schedule) {
        switch (schedule) {
            case "Sabah":
                caution = schedule + " tarifesine geçmelisiniz.";
                break;
            case "Puant":
                caution = schedule + " tarifesine geçmelisiniz.";
                break;
            case "Gece":
                caution = schedule + " tarifesine geçmelisiniz.";
                break;
        }
        return caution;
    }
}
