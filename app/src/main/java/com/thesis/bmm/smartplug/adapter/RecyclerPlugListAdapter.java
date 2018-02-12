package com.thesis.bmm.smartplug.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Plugs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MUHAMMED on 23.01.2018.
 */

public class RecyclerPlugListAdapter extends RecyclerView.Adapter<RecyclerPlugListViewHolder> {
    List<Plugs> plugsList;
    Context context;

    public RecyclerPlugListAdapter(ArrayList<Plugs> plugsList, Context context) {
        this.plugsList = plugsList;
        this.context = context;
    }


    @Override
    public RecyclerPlugListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new RecyclerPlugListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerPlugListViewHolder holder, final int position) {
        holder.txtRoomName.setText(plugsList.get(position).getPlugRoom());
        holder.txtPlugName.setText(plugsList.get(position).getPlugName());
        holder.plugStatus.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Plugs").child(plugsList.get(position).getPlugID());
                        if (isChecked) {
                            Plugs plugs = new Plugs(plugsList.get(position).getPlugID(), plugsList.get(position).getPlugName(), plugsList.get(position).getPlugRoom(), plugsList.get(position).getPlugCurrent(), true);
                            dr.setValue(plugs);
                        } else {
                            Plugs plugs = new Plugs(plugsList.get(position).getPlugID(), plugsList.get(position).getPlugName(), plugsList.get(position).getPlugRoom(), plugsList.get(position).getPlugCurrent(), false);
                            dr.setValue(plugs);
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return plugsList.size();
    }
}
