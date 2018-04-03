package com.thesis.bmm.smartplug.adapter;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;

/**
 * Created by MUHAMMED on 13.03.2018.
 */

public class RecyclerLocationListViewHolder extends RecyclerView.ViewHolder {
    public TextView adress;
    public SwitchCompat notification_status;
    public AppCompatImageView update, delete;

    public RecyclerLocationListViewHolder(View itemView) {
        super(itemView);
        adress = itemView.findViewById(R.id.adress_content);
        notification_status = itemView.findViewById(R.id.notification_switch);
        update = itemView.findViewById(R.id.button_cityupdate);
        delete = itemView.findViewById(R.id.button_citydelete);
        delete.setImageResource(R.drawable.ic_delete_black_24dp);
        update.setImageResource(R.drawable.ic_settings_backup_restore_black_24dp);
    }
}
