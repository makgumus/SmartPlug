package com.thesis.bmm.smartplug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.model.Plugs;

public class EditPlugDialog {
    private ArrayAdapter spinnerRoomAdapter;
    private Context context;

    //int status=1  >> AddPlug
    //int status=0  >> UpdatePlug
    public EditPlugDialog(Context context) {
        this.context = context;
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
        spinnerRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spn = new Spinner(context);
        spn.setAdapter(spinnerRoomAdapter);
        tvMessage[0].setText("Odayı Seçiniz");
        tvMessage[1].setText("Prizin Adını Girin");
        final EditText plugName = new EditText(context);
        for (int i = 0; i < 2; i++) {
            tvMessage[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        }
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tvMessage[0]);
        layout.addView(spn);
        layout.addView(tvMessage[1]);
        layout.addView(plugName);
        layout.setPadding(50, 40, 50, 10);
        builder.setView(layout);
        builder.setNegativeButton("" + context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("" + context.getResources().getString(R.string.okey), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (plugName.getText().toString().length() > 0) {
                    if (status == 1) {
                        addNewPlugatFirebase(plugName.getText().toString(), spn.getSelectedItem().toString());
                    } else {
                        updatePlugatFirebase(PlugID, plugName.getText().toString(), spn.getSelectedItem().toString());
                    }
                } else {
                    showAlertDialog();
                }

            }
        });
        builder.create().show();
    }

    private void addNewPlugatFirebase(String plugName, String roomName) //Firebase'de yeni bir priz açma
    {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("Plugs");
        String id = databaseReferencePlug.push().getKey();
        Plugs plug = new Plugs(id, plugName.toString(), roomName.toString(), "0", false);
        databaseReferencePlug.child(id).setValue(plug);
    }

    public void updatePlugatFirebase(String plugID, String plugName, String roomName) {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("Plugs").child(plugID);
        Plugs plug = new Plugs(plugID, plugName, roomName, "0", false);
        databaseReferencePlug.setValue(plug);
    }

    public void deletePlugatFirebase(String id) {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("Plugs").child(id);
        databaseReferencePlug.removeValue();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("" + context.getResources().getString(R.string.error));
        builder.setMessage("" + context.getResources().getString(R.string.pleaseenterthenameoftheplug));
        String positiveText = "" + context.getResources().getString(R.string.okey);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
