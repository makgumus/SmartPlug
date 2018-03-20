package com.thesis.bmm.smartplug.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Plugs;

import java.util.ArrayList;

public class GraphicActivity extends AppCompatActivity {
    Context context = this;
    DatabaseReference drPlugs = FirebaseDatabase.getInstance().getReference().child("Plugs");
    TextView plugCurrentText;
    ArrayList<Entry> entries;
    int x = 0;
    private LineChart realTimeCurrentGraph;
    private String plugId, plugLiveCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        initView();
    }

    private void initView() {
        realTimeCurrentGraph = findViewById(R.id.realTimeCurrentGraph);
        plugCurrentText = findViewById(R.id.currentText);
        initEvent();
    }

    private void initEvent() {
        Bundle extras = getIntent().getExtras();
        plugId = extras.getString("plugID");
        drawCurrentGraph();
        currentDataUpdate();
    }

    private void drawCurrentGraph() {
        entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        realTimeCurrentGraph.setData(initLiveChartData(entries));
        realTimeCurrentGraph.setTouchEnabled(true);
        realTimeCurrentGraph.setDragEnabled(true);
        realTimeCurrentGraph.setScaleEnabled(true);
        realTimeCurrentGraph.setDrawGridBackground(false);
        realTimeCurrentGraph.setExtraOffsets(10, 20, 20, 10);
        realTimeCurrentGraph.getDescription().setEnabled(false);
        realTimeCurrentGraph.getLegend().setEnabled(false);
        realTimeCurrentGraph.setPinchZoom(false);
        realTimeCurrentGraph.setVisibleXRangeMaximum(25);
        realTimeCurrentGraph.animateX(1000);
        realTimeCurrentGraph.getXAxis().setEnabled(false);
        realTimeCurrentGraph.getAxisRight().setEnabled(false);
        realTimeCurrentGraph.getAxisLeft().setGridColor(Color.rgb(130, 129, 135));
        realTimeCurrentGraph.getAxisLeft().setTextColor(Color.rgb(130, 129, 135));
        realTimeCurrentGraph.getAxisLeft().setDrawAxisLine(false);
        realTimeCurrentGraph.getAxisLeft().setAxisMinimum(0f);
        realTimeCurrentGraph.getAxisLeft().setEnabled(true);
    }

    private LineData initLiveChartData(ArrayList<Entry> entries) {

        LineDataSet dataset = new LineDataSet(entries, "1st plug");
        dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataset.setColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
        // dataset.setColor(ResourcesCompat.getColor(getResources(), R.color.lightbg, null));
        dataset.setLineWidth(1.5f);
        dataset.setDrawCircles(true);
        dataset.setCircleRadius(1f);
        dataset.setDrawValues(false);
        dataset.setFillAlpha(65);
        // dataset.setFillColor(Color.rgb(62,163,171));
        dataset.setFillColor(ResourcesCompat.getColor(getResources(), R.color.lightbg, null));
        dataset.setHighLightColor(Color.rgb(244, 117, 117));
        dataset.setDrawCircleHole(false);
        return new LineData(dataset);
    }

    private void addEntry(String current) {

        LineData data = realTimeCurrentGraph.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well
            if (set != null) {
                data.addEntry(new Entry(set.getEntryCount(), Float.parseFloat(current)), 0);
                data.notifyDataChanged();
                realTimeCurrentGraph.notifyDataSetChanged();
                realTimeCurrentGraph.setVisibleXRangeMaximum(50);
                realTimeCurrentGraph.moveViewToX(data.getEntryCount());

            } else {
                Log.d("donmak", "set is null");
            }
        }
    }

    private void currentDataUpdate() {
        drPlugs = drPlugs.child(plugId);
        drPlugs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Plugs plugs = dataSnapshot.getValue(Plugs.class);
                plugLiveCurrent = plugs.getPlugCurrent();
                plugCurrentText.setText(plugLiveCurrent);
                // addEntry(plugLiveCurrent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        drPlugs.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addEntry(plugLiveCurrent);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addEntry(plugLiveCurrent);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
