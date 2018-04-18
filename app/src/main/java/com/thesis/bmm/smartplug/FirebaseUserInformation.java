package com.thesis.bmm.smartplug;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirebaseUserInformation {
    private Context context;
    private SharedPreferences sharedPreferences;
    private String firebaseUserId;


    public FirebaseUserInformation(Context context) {
        this.context = context;
    }

    public String getFirebaseUserId() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        firebaseUserId = sharedPreferences.getString("userID", "Yok");
        return firebaseUserId;
    }
}
