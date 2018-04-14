package com.thesis.bmm.smartplug.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.ElectricitySchedule;
import com.thesis.bmm.smartplug.model.Plugs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GraphicActivity extends AppCompatActivity {
    Context context = this;
    ArrayList<Entry> entries;
    private ArrayList<ElectricitySchedule> electricitySchedulesList;
    private LineChart realTimeCurrentGraph;
    private PieChart dailyCurrentGraph;
    private String plugId, plugLiveCurrent;
    private AppCompatImageView previousDayButton, nextDayButton;
    private DatabaseReference dr, drPlugs;
    private TextView calendarTextView, plugCurrentText, useOfMonthlyEnergy;
    private Calendar calendar;
    private SimpleDateFormat df;
    SharedPreferences sharedPreferences ;
    String  datauserid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        datauserid= sharedPreferences.getString("userID", "Yok") ;
        initView();
    }

    private void initView() {
        realTimeCurrentGraph = findViewById(R.id.realTimeCurrentGraph);
        plugCurrentText = findViewById(R.id.currentText);
        dailyCurrentGraph = findViewById(R.id.dailyCurrentGraph);
        previousDayButton = findViewById(R.id.beforeBtn);
        nextDayButton = findViewById(R.id.afterBtn);
        calendarTextView = findViewById(R.id.dateTV);
        useOfMonthlyEnergy = findViewById(R.id.energyTV);
        nextDayButton.setImageResource(R.drawable.ic_chevron_right_black_24dp);
        previousDayButton.setImageResource(R.drawable.ic_chevron_left_black_24dp);
        initPieChartStyle();
        initEvent();
    }

    private void initEvent() {
        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("MM");
        Bundle extras = getIntent().getExtras();
        plugId = extras.getString("plugID");
        drPlugs = FirebaseDatabase.getInstance().getReference().child(""+datauserid).child("Plugs");
        String thisMonth = df.format(calendar.getTime());
        calendarTextView.setText(convertToMonth(thisMonth));
        getPieChartData(checkMonthandDay(thisMonth));
        drawCurrentGraph();
        currentDataUpdate();
        previousDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                String previousMonth = df.format(calendar.getTime());
                calendarTextView.setText(convertToMonth(previousMonth));
                getPieChartData(checkMonthandDay(previousMonth));
            }
        });
        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, +1);
                String nextMonth = df.format(calendar.getTime());
                calendarTextView.setText(convertToMonth(nextMonth));
                getPieChartData(checkMonthandDay(nextMonth));
            }
        });
    }

    public String checkMonthandDay(String value) {
        if (value.substring(0, 1).equals("0")) {
            value = value.substring(1, value.length());
        }
        return value;
    }

    private String convertToMonth(String monthOfYear) {
        String value = null;
        switch (monthOfYear) {
            case "01":
                value = ""+getResources().getString(R.string.january);
                break;
            case "02":
                value = ""+getResources().getString(R.string.february);
                break;
            case "03":
                value = ""+getResources().getString(R.string.march);
                break;
            case "04":
                value = ""+getResources().getString(R.string.april);
                break;
            case "05":
                value = ""+getResources().getString(R.string.may);
                break;
            case "06":
                value = ""+getResources().getString(R.string.june);
                break;
            case "07":
                value = ""+getResources().getString(R.string.july);
                break;
            case "08":
                value = ""+getResources().getString(R.string.august);
                break;
            case "09":
                value = ""+getResources().getString(R.string.september);
                break;
            case "10":
                value = ""+getResources().getString(R.string.october);
                break;
            case "11":
                value = ""+getResources().getString(R.string.november);
                break;
            case "12":
                value = ""+getResources().getString(R.string.december);
                break;
        }
        return value;
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

    private void getPieChartData(final String month) {
        electricitySchedulesList = new ArrayList<>();
        dr = FirebaseDatabase.getInstance().getReference(""+datauserid).child("PieChartData").child(plugId).child(month);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                electricitySchedulesList.clear();
                ElectricitySchedule electricitySchedule = dataSnapshot.getValue(ElectricitySchedule.class);
                electricitySchedulesList.add(electricitySchedule);
                drawPieChart(electricitySchedulesList.get(0).getT1(), electricitySchedulesList.get(0).getT2(), electricitySchedulesList.get(0).getT3());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void drawPieChart(String t1, String t2, String t3) {
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(Float.parseFloat(t1), "T1"));
        yValues.add(new PieEntry(Float.parseFloat(t2), "T2"));
        yValues.add(new PieEntry(Float.parseFloat(t3), "T3"));
        PieDataSet dataSet = new PieDataSet(yValues, ""+getResources().getString(R.string.elektricrange));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        dailyCurrentGraph.setData(pieData);
        dailyCurrentGraph.invalidate();
    }

    private void initPieChartStyle() {
        dailyCurrentGraph.setUsePercentValues(true);
        dailyCurrentGraph.getDescription().setEnabled(false);
        dailyCurrentGraph.setExtraOffsets(5, 10, 5, 5);
        dailyCurrentGraph.setDrawHoleEnabled(true);
        dailyCurrentGraph.setHoleColor(Color.WHITE);
        dailyCurrentGraph.setTransparentCircleRadius(61f);
    }

}
