package com.thesis.bmm.smartplug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.model.ElectricitySchedule;
import com.thesis.bmm.smartplug.model.Plugs;

public class EditPlugDialog {
    private ArrayAdapter spinnerRoomAdapter, spinnerPlugNameAdapter;
    private Context context;
    private DatabaseReference databaseReferencePlug;
    private DatabaseReference dr;

    //int status=1  >> AddPlug
    //int status=0  >> UpdatePlug
    public EditPlugDialog(Context context) {
        this.context = context;
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        databaseReferencePlug = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Plugs");
        dr = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("PieChartData");
    }

    public void selectPlugDialog(final int status, final String PlugID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        int textViewCount = 2;
        TextView[] tvMessage = new TextView[textViewCount];
        for (int i = 0; i < textViewCount; i++) {
            tvMessage[i] = new TextView(context);
        }
        spinnerRoomAdapter = ArrayAdapter.createFromResource(context, R.array.array_name, android.R.layout.simple_spinner_item);
        spinnerPlugNameAdapter = ArrayAdapter.createFromResource(context, R.array.array_plugName, android.R.layout.simple_spinner_item);
        spinnerRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spn[] = new Spinner[2];
        for (int i = 0; i < spn.length; i++) {
            spn[i] = new Spinner(context);
        }
        spn[0].setAdapter(spinnerRoomAdapter);
        spn[1].setAdapter(spinnerPlugNameAdapter);
        tvMessage[0].setText("" + context.getResources().getString(R.string.chooseroom));
        tvMessage[1].setText("" + context.getResources().getString(R.string.enterthenameoftheplug));
        for (int i = 0; i < 2; i++) {
            tvMessage[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        }
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 2; i++) {
            layout.addView(tvMessage[i]);
            layout.addView(spn[i]);
        }
        layout.setPadding(50, 40, 50, 10);
        builder.setView(layout);
        builder.setNegativeButton("" + context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("" + context.getResources().getString(R.string.okey), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (status == 1) {
                    addNewPlugatFirebase(spn[1].getSelectedItem().toString(), spn[0].getSelectedItem().toString());
                } else {
                    updatePlugatFirebase(PlugID, spn[1].getSelectedItem().toString(), spn[0].getSelectedItem().toString());
                }
            }
        });
        builder.create().show();
    }

    private void addNewPlugatFirebase(String plugName, String roomName) //Firebase'de yeni bir priz açma
    {
        String id = databaseReferencePlug.push().getKey();
        Plugs plug = new Plugs(id, plugName.toString(), roomName.toString(), "0", false);
        databaseReferencePlug.child(id).setValue(plug);
        chartDataatFirebase(id);
    }

    public void updatePlugatFirebase(String plugID, String plugName, String roomName) {
        Plugs plug = new Plugs(plugID, plugName, roomName, "0", false);
        databaseReferencePlug.child(plugID).setValue(plug);
    }

    public void deletePlugatFirebase(String id) {
        databaseReferencePlug.child(id).removeValue();
        dr.child(id).removeValue();
    }

    private void chartDataatFirebase(String id) {
        for (int i = 1; i <= 12; i++) {
            ElectricitySchedule schedule = new ElectricitySchedule("0", "0", "0", "0", "0");
            dr.child(id).child(String.valueOf(i)).setValue(schedule);
        }

    }

}
