package com.thesis.bmm.smartplug.adapter;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;

/**
 * Created by MUHAMMED on 13.03.2018.
 */

public class RecyclerLocationListViewHolder extends RecyclerView.ViewHolder {
    public TextView adress;
    public Switch notification_status;
    public FloatingActionButton update, delete;

    public RecyclerLocationListViewHolder(View itemView) {
        super(itemView);
        adress = itemView.findViewById(R.id.adress_content);
        notification_status = itemView.findViewById(R.id.notification_switch);
        update = itemView.findViewById(R.id.button_cityupdate);
        delete = itemView.findViewById(R.id.button_citydelete);
    }
}
