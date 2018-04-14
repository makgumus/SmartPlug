package com.thesis.bmm.smartplug.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.InterruptListAdapter;
import com.thesis.bmm.smartplug.model.ElectricityInterrupt;
import com.thesis.bmm.smartplug.model.Locations;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    private ListView interruptsListView;
    private View view;
    private ArrayList<Locations> locationsList;
    private ArrayList<String> interruptList;
    private InterruptRequest interruptRequest;
    private ArrayList<ElectricityInterrupt> list;

    public NotificationFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        initView();
        return view;
    }

    private void initView() {
        interruptsListView = view.findViewById(R.id.list);
        locationsList = new ArrayList<>();
        interruptList = new ArrayList<String>();
        getInterruptDatafromFirebase();
    }

    private void getInterruptDatafromFirebase() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String  datauserid= sharedPreferences.getString("userID", "Yok") ;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(""+datauserid).child("Interrupts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                interruptList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String interruptExplain = postSnapshot.getValue().toString();
                    interruptList.add(interruptExplain);
                }
                InterruptListAdapter adapter = new InterruptListAdapter(interruptList, getContext());
                interruptsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
