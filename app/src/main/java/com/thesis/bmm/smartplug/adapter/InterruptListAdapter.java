package com.thesis.bmm.smartplug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;

import java.util.ArrayList;

/**
 * Created by MUHAMMED on 5.03.2018.
 */

public class InterruptListAdapter extends BaseAdapter {
    ArrayList<String> interruptList;
    private LayoutInflater layoutInflater;
    private Context context;
    private TextView content;

    public InterruptListAdapter(ArrayList<String> interruptList, Context context) {
        this.interruptList = interruptList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return interruptList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.interrupt_item, null);
        content = convertView.findViewById(R.id.contentInterrupt);
        content.setText(interruptList.get(position).toString());
        return convertView;
    }

}
