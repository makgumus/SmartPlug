package com.thesis.bmm.smartplug.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.activities.GraphicActivity;
import com.thesis.bmm.smartplug.model.Plugs;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RealTimeCurrentListenerService extends Service {
    private static float T1 = 0, T2 = 0, T3 = 0, totalEnergy = 0, totalCost = 0;
    private Calendar now;
    private SimpleDateFormat dfMonth;
    private DatabaseReference drPlugs, drPieChartData;
    private Plugs plug;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dfMonth = new SimpleDateFormat("MM");
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(getApplicationContext());
        drPlugs = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs");
        drPieChartData = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("PieChartData");
        getCurrent();
    }

    private float calculateEnergy(float current) {
        float energyConsumption = 0;
        float watt = current * 240;
        float hour = 5 / 3600; //0.0013888888888889
        energyConsumption += watt / 1000 * 0.0013888888888889;
        return energyConsumption;
    }

    private void getCurrent() {

        drPlugs.child("-LAK7L98xTjGxY7dCtKf").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plug = dataSnapshot.getValue(Plugs.class);
                now = Calendar.getInstance();
                GraphicActivity graphicActivity = new GraphicActivity();
                final String month = graphicActivity.checkMonthandDay(dfMonth.format(now.getTime()));
                if (Float.parseFloat(plug.getPlugCurrent()) >= 0) {
                    int nowHour = now.get(Calendar.HOUR_OF_DAY);
                    if (6 <= nowHour && nowHour < 17) {
                        T1 += calculateEnergy(Float.parseFloat(plug.getPlugCurrent()));
                        drPieChartData.child("-LAK7L98xTjGxY7dCtKf").child(month).child("t1").setValue(String.valueOf(T1));
                    }
                    if (17 <= nowHour && nowHour < 22) {
                        T2 += calculateEnergy(Float.parseFloat(plug.getPlugCurrent()));
                        drPieChartData.child("-LAK7L98xTjGxY7dCtKf").child(month).child("t2").setValue(String.valueOf(T2));
                    }
                    if (22 <= nowHour || nowHour < 6) {
                        T3 += calculateEnergy(Float.parseFloat(plug.getPlugCurrent()));
                        drPieChartData.child("-LAK7L98xTjGxY7dCtKf").child(month).child("t3").setValue(String.valueOf(T3));
                    }
                    totalEnergy = T1 + T2 + T3;
                    totalCost = (float) (T1 * 0.4463 + T2 * 0.6769 + T3 * 0.2797);
                    drPieChartData.child("-LAK7L98xTjGxY7dCtKf").child(month).child("totalEnergyConsumption").setValue(String.valueOf(String.format("%.2f", totalEnergy)));
                    drPieChartData.child("-LAK7L98xTjGxY7dCtKf").child(month).child("cost").setValue(String.valueOf(String.format("%.2f", totalCost)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
