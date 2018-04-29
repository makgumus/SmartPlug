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
import com.thesis.bmm.smartplug.model.ElectricitySchedule;
import com.thesis.bmm.smartplug.model.Plugs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class RealTimeCurrentListenerService extends Service {
    private float T1, T2, T3, totalT1, totalT2, totalT3, totalEnergy, totalCost;
    private Calendar now;
    private ArrayList<Plugs> plugList;
    private SimpleDateFormat dfMonth;
    private DatabaseReference drPlugs, drPieChartData, drUser;
    private GraphicActivity graphicActivity;

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
        plugList = new ArrayList<>();
        dfMonth = new SimpleDateFormat("MM");
        graphicActivity = new GraphicActivity();
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(getApplicationContext());
        drUser = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId());
        drPlugs = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs");
        drPieChartData = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("PieChartData");
        drPlugs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plugList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Plugs plug = postSnapshot.getValue(Plugs.class);
                    getCurrent(plug.getPlugID(), plug.getPlugCurrent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private float calculateEnergy(float current) {
        float energyConsumption = 0;
        float watt = current * 240;
        float hour = 5 / 3600; //0.0013888888888889
        energyConsumption += watt / 1000 * 0.0013888888888889;
        return energyConsumption;
    }

    private void getCurrent(final String plugID, final String plugCurrent) {
        now = Calendar.getInstance();
        final String month = graphicActivity.checkMonthandDay(dfMonth.format(now.getTime()));
        if (Float.parseFloat(plugCurrent) > 0) {
            drPieChartData.child(plugID).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int nowHour = now.get(Calendar.HOUR_OF_DAY);
                    ElectricitySchedule es = dataSnapshot.getValue(ElectricitySchedule.class);
                    if (6 <= nowHour && nowHour < 17) {
                        T1 = Float.parseFloat(es.getT1());
                        T1 += calculateEnergy(Float.parseFloat(plugCurrent));
                        totalEnergy = T1 + Float.parseFloat(es.getT2()) + Float.parseFloat(es.getT3());
                        totalCost = (float) (T1 * 0.4463 + Float.parseFloat(es.getT2()) * 0.6769 + Float.parseFloat(es.getT3()) * 0.2797);
                        drPieChartData.child(plugID).child(month).child("t1").setValue(String.valueOf(T1));
                        drUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals("totalT1")) {
                                        String t1 = postSnapshot.getValue().toString();
                                        totalT1 = Float.parseFloat(t1);
                                        drUser.child("totalT1").setValue(String.valueOf(totalT1 + T1));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if (17 <= nowHour && nowHour < 22) {
                        T2 = Float.parseFloat(es.getT2());
                        T2 += calculateEnergy(Float.parseFloat(plugCurrent));
                        totalEnergy = T2 + Float.parseFloat(es.getT1()) + Float.parseFloat(es.getT3());
                        totalCost = (float) (T2 * 0.4463 + Float.parseFloat(es.getT1()) * 0.6769 + Float.parseFloat(es.getT3()) * 0.2797);
                        drPieChartData.child(plugID).child(month).child("t2").setValue(String.valueOf(T2));
                        drUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals("totalT2")) {
                                        String t2 = postSnapshot.getValue().toString();
                                        totalT2 = Float.parseFloat(t2);
                                        drUser.child("totalT2").setValue(String.valueOf(totalT2 + T2));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if (22 <= nowHour || nowHour < 6) {
                        T3 = Float.parseFloat(es.getT3());
                        T3 += calculateEnergy(Float.parseFloat(plugCurrent));
                        totalEnergy = T3 + Float.parseFloat(es.getT1()) + Float.parseFloat(es.getT2());
                        totalCost = (float) (T3 * 0.4463 + Float.parseFloat(es.getT1()) * 0.6769 + Float.parseFloat(es.getT2()) * 0.2797);
                        drPieChartData.child(plugID).child(month).child("t3").setValue(String.valueOf(T3));
                        drUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals("totalT3")) {
                                        String t3 = postSnapshot.getValue().toString();
                                        totalT3 = Float.parseFloat(t3);
                                        drUser.child("totalT3").setValue(String.valueOf(totalT3 + T3));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    drPieChartData.child(plugID).child(month).child("totalEnergyConsumption").setValue(String.valueOf(String.format("%.2f", totalEnergy)));
                    drPieChartData.child(plugID).child(month).child("cost").setValue(String.valueOf(String.format("%.2f", totalCost)));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }


}
