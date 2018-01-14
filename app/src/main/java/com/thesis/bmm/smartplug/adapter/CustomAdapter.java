package com.thesis.bmm.smartplug.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.thesis.bmm.smartplug.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends BaseAdapter
{
    LayoutInflater mInlfater;
    ArrayList<HashMap<String,String>> list;
    public CustomAdapter(Context context, ArrayList<HashMap<String, String>> list)
    {
        mInlfater = LayoutInflater.from(context);
        this.list =list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if(convertView ==null)
        {
            convertView = mInlfater.inflate(R.layout.list_item,null);
            holder = new ViewHolder();
            holder.b1 = convertView.findViewById(R.id.sw);
            holder.tv1 = convertView.findViewById(R.id.textView1);
            holder.tv2 = convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder) convertView.getTag();
        }
        HashMap<String,String> map = list.get(position);
        holder.tv1.setText(map.get("Priz"));
        holder.tv2.setText(map.get("AkımDegeri"));
        holder.b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return convertView;
    }
    static class ViewHolder
    {
        SwitchCompat b1;
        TextView tv1,tv2,tv3;
    }
}
