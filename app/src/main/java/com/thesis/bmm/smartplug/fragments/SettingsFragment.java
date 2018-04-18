package com.thesis.bmm.smartplug.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.LocationRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.RecyclerLocationListAdapter;
import com.thesis.bmm.smartplug.model.Locations;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {
    private View view;
    private AppCompatSpinner spntarife;
    private Button btntarifechange;
    private LocationRequest locationRequest;
    private FloatingActionButton locationAdd;
    private DatabaseReference locationDatabaseReference;
    private ArrayList<Locations> locationsList;
    private RecyclerLocationListAdapter recyclerLocationListAdapter;
    private RecyclerView recyclerLocationsListView;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        locationDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Locations locations = postSnapshot.getValue(Locations.class);
                    locationsList.add(locations);
                }
                recyclerLocationsListView.setHasFixedSize(true);
                recyclerLayoutManager = new LinearLayoutManager(getActivity());
                recyclerLocationsListView.setLayoutManager(recyclerLayoutManager);
                recyclerLocationListAdapter = new RecyclerLocationListAdapter(locationsList, getActivity());
                recyclerLocationsListView.setAdapter(recyclerLocationListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }


    private void initView(View views) {
        locationAdd = views.findViewById(R.id.button_adress_add);
        spntarife=views.findViewById(R.id.spntarife);
        btntarifechange=views.findViewById(R.id.btntarifechange);
        AppCompatImageView image2 = views.findViewById(R.id.iv_about2);
        AppCompatImageView image3 =views.findViewById(R.id.iv_about3);
        image2.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        image3.setImageResource(R.drawable.ic_account_circle_black_24dp);
        recyclerLocationsListView = views.findViewById(R.id.recycler_locationsList);
        initEvent();
    }

    private void initEvent() {
        locationsList = new ArrayList<>();
        final FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(getContext());
        locationDatabaseReference = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Locations");
        locationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRequest = new LocationRequest(getContext());
                locationRequest.selectAdressDialog(1, "null");
            }
        });
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("usertarife");
        databaseReferencePlug.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value!=null) {
                    if (value.equals("T1")) {
                      spntarife.setSelection(1);
                    }
                    if (value.equals("T2")) {
                        spntarife.setSelection(2);
                    }
                    if (value.equals("T3")) {
                        spntarife.setSelection(3);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btntarifechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("usertarife");
                if(spntarife.getSelectedItemPosition()==1)
                {
                    databaseReferencePlug.setValue("T1");
                }
                if(spntarife.getSelectedItemPosition()==2)
                {
                    databaseReferencePlug.setValue("T2");
                }
                if(spntarife.getSelectedItemPosition()==3)
                {
                    databaseReferencePlug.setValue("T3");
                }
            }
        });
    }


}
