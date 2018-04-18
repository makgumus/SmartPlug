package com.thesis.bmm.smartplug.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.EditPlugDialog;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.RecyclerPlugListAdapter;
import com.thesis.bmm.smartplug.model.Plugs;
import com.thesis.bmm.smartplug.services.RealTimeCurrentListenerService;

import java.util.ArrayList;


public class PlugsFragment extends Fragment {
    private RecyclerView recyclerPlugsListView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private DatabaseReference databaseReferencePlugs;
    private RecyclerPlugListAdapter plugListAdapter;
    private ArrayList<Plugs> plugsList;
    private View view;
    private FloatingActionButton addNewPlugButton;
    private static int workingNow = 0;


    public PlugsFragment() {
        // Required empty public constructor
    }

    public static PlugsFragment newInstance(String param1, String param2) {
        PlugsFragment fragment = new PlugsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        plugsList = new ArrayList<>();
        databaseReferencePlugs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plugsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Plugs plug = postSnapshot.getValue(Plugs.class);
                    plugsList.add(plug);
                }
                recyclerPlugsListView.setHasFixedSize(true);
                recyclerLayoutManager = new LinearLayoutManager(getActivity());
                recyclerPlugsListView.setLayoutManager(recyclerLayoutManager);
                plugListAdapter = new RecyclerPlugListAdapter(plugsList, getActivity());
                recyclerPlugsListView.setAdapter(plugListAdapter);
                if (plugsList.size() != 0 && workingNow == 0) {
                    workingNow = 1;
                    checkCurrentListenerService(1);
                }
                if (workingNow == 1 && plugsList.size() == 0) {
                    workingNow = 0;
                    checkCurrentListenerService(0);
                }
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

        view = inflater.inflate(R.layout.fragment_plugs, container, false);
        initView();
        return view;

    }

    private void checkCurrentListenerService(int status) {
        Intent intent = new Intent(getContext(), RealTimeCurrentListenerService.class);
        if (status == 1) {
            getContext().startService(intent);
        } else {
            getContext().stopService(intent);
        }

    }

    private void initView() {
        recyclerPlugsListView = view.findViewById(R.id.recycler_plugsList);
        addNewPlugButton = view.findViewById(R.id.addNewPlugButton);
        initEvent();
    }

    private void initEvent() {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(getContext());
        databaseReferencePlugs = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs");
        addNewPlugButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), NewPlugActivity.class);
                //startActivity(intent);
                EditPlugDialog plugDialog = new EditPlugDialog(getActivity());
                plugDialog.selectPlugDialog(1, null);
            }
        });
    }

}
