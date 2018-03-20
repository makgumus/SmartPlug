package com.thesis.bmm.smartplug.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.InterruptRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.InterruptListAdapter;
import com.thesis.bmm.smartplug.model.Locations;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    private ListView interruptsList;
    private Button btn;
    private View view;
    private String province, district, region;
    private ArrayList<Locations> locationsList;

    public NotificationFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        getDatafromFirebase();
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
        interruptsList = view.findViewById(R.id.list);
        locationsList = new ArrayList<>();
        if (locationsList != null) {
            for (int i = 0; i < locationsList.size(); i++) {
                Locations location = locationsList.get(i);
                InterruptRequest interruptRequest = new InterruptRequest(this.getContext(), location.getProvince().toString(), location.getDistrict().toString(), location.getRegion().toString());
                interruptRequest.request();
                InterruptListAdapter adapter = new InterruptListAdapter(interruptRequest.electricityInterruptList, getContext());
                interruptsList.setAdapter(adapter);
            }

        }
    }

    private void getDatafromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Locations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Locations location = postSnapshot.getValue(Locations.class);
                    locationsList.add(location);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
