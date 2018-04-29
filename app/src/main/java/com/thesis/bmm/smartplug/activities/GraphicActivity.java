package com.thesis.bmm.smartplug.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.FirebaseUserInformation;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.ElectricitySchedule;
import com.thesis.bmm.smartplug.model.Plugs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GraphicActivity extends AppCompatActivity {
    ArrayList<Entry> entries;
    private LineChart realTimeCurrentGraph;
    private PieChart dailyCurrentGraph;
    private String plugId, plugLiveCurrent;
    private ImageView previousDayButton, nextDayButton, backMainActivity;
    private DatabaseReference drPieChartData, drPlugs;
    private TextView calendarTextView, plugCurrentText, useOfMonthlyEnergy, useOfMonthlyCost;
    private Calendar calendar;
    private SimpleDateFormat df;
    private ValueEventListener drPlugsListener, drPieChartListener;
    private ArrayList<String> openedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
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
        useOfMonthlyCost = findViewById(R.id.costTV);
        backMainActivity = findViewById(R.id.back_btn);
        initPieChartStyle();
        initEvent();
    }

    private void initEvent() {
        calendar = Calendar.getInstance();
        df = new SimpleDateFormat("MM");
        Bundle extras = getIntent().getExtras();
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(getApplicationContext());
        plugId = extras.getString("plugID");
        drPlugs = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs").child(plugId);
        drPieChartData = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("PieChartData").child(plugId);
        String thisMonth = df.format(calendar.getTime());
        calendarTextView.setText(convertToMonth(thisMonth));
        getPieChartData(checkMonthandDay(thisMonth));
        openedMonth = new ArrayList();
        openedMonth.add(checkMonthandDay(thisMonth));
        drawCurrentGraph();
        currentDataUpdate();
        previousDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                String previousMonth = df.format(calendar.getTime());
                calendarTextView.setText(convertToMonth(previousMonth));
                getPieChartData(checkMonthandDay(previousMonth));
                openedMonth.add(checkMonthandDay(previousMonth));
            }
        });
        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, +1);
                String nextMonth = df.format(calendar.getTime());
                calendarTextView.setText(convertToMonth(nextMonth));
                getPieChartData(checkMonthandDay(nextMonth));
                openedMonth.add(checkMonthandDay(nextMonth));
            }
        });
        backMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public String checkMonthandDay(String value) {
        if (value.substring(0, 1).equals("0")) {
            value = value.substring(1, value.length());
        }
        return value;
    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < openedMonth.size(); i++) {
            drPieChartData.child(openedMonth.get(i)).removeEventListener(drPieChartListener);
        }
        drPlugs.removeEventListener(drPlugsListener);
        super.onBackPressed();
        finish();
    }

    private String convertToMonth(String monthOfYear) {
        String value = null;
        switch (monthOfYear) {
            case "01":
                value = "" + getResources().getString(R.string.january);
                break;
            case "02":
                value = "" + getResources().getString(R.string.february);
                break;
            case "03":
                value = "" + getResources().getString(R.string.march);
                break;
            case "04":
                value = "" + getResources().getString(R.string.april);
                break;
            case "05":
                value = "" + getResources().getString(R.string.may);
                break;
            case "06":
                value = "" + getResources().getString(R.string.june);
                break;
            case "07":
                value = "" + getResources().getString(R.string.july);
                break;
            case "08":
                value = "" + getResources().getString(R.string.august);
                break;
            case "09":
                value = "" + getResources().getString(R.string.september);
                break;
            case "10":
                value = "" + getResources().getString(R.string.october);
                break;
            case "11":
                value = "" + getResources().getString(R.string.november);
                break;
            case "12":
                value = "" + getResources().getString(R.string.december);
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

    @Override
    protected void onPause() {
        for (int i = 0; i < openedMonth.size(); i++) {
            drPieChartData.child(openedMonth.get(i)).removeEventListener(drPieChartListener);
        }
        drPlugs.removeEventListener(drPlugsListener);
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        for (int i = 0; i < openedMonth.size(); i++) {
            drPieChartData.child(openedMonth.get(i)).removeEventListener(drPieChartListener);
        }
        drPlugs.removeEventListener(drPlugsListener);
        super.onStop();
        finish();
    }

    private void currentDataUpdate() {
        drPlugsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Plugs plugs = dataSnapshot.getValue(Plugs.class);
                plugLiveCurrent = plugs.getPlugCurrent();
                plugCurrentText.setText("Akım:" + plugLiveCurrent + " A");
                addEntry(plugLiveCurrent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        drPlugs.addValueEventListener(drPlugsListener);
    }

    private void getPieChartData(final String month) {
        drPieChartListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ElectricitySchedule electricitySchedule = dataSnapshot.getValue(ElectricitySchedule.class);
                drawPieChart(electricitySchedule.getT1(), electricitySchedule.getT2(), electricitySchedule.getT3());
                useOfMonthlyEnergy.setText(electricitySchedule.getTotalEnergyConsumption());
                useOfMonthlyCost.setText(electricitySchedule.getCost() + "₺");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        drPieChartData.child(month).addValueEventListener(drPieChartListener);
    }

    private void drawPieChart(String t1, String t2, String t3) {
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(Float.parseFloat(t1), "Sabah"));
        yValues.add(new PieEntry(Float.parseFloat(t2), "Puant"));
        yValues.add(new PieEntry(Float.parseFloat(t3), "Gece"));
        PieDataSet dataSet = new PieDataSet(yValues, "Elektrik Kullanım Aralıkları");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
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
