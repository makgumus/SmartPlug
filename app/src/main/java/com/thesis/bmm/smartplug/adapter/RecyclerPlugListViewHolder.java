package com.thesis.bmm.smartplug.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;

/**
 * Created by MUHAMMED on 23.01.2018.
 */

public class RecyclerPlugListViewHolder extends RecyclerView.ViewHolder {
    public TextView txtRoomName = null, txtPlugName = null;
    public SwitchCompat plugStatus;
    public ImageView currentGraphicInfo;

    public RecyclerPlugListViewHolder(View itemView) {
        super(itemView);
        txtRoomName = this.itemView.findViewById(R.id.roomName);
        txtPlugName = this.itemView.findViewById(R.id.plugName);
        plugStatus = this.itemView.findViewById(R.id.switch_plugStatus);
        currentGraphicInfo = this.itemView.findViewById(R.id.currentGraphInfo);

    }

}
