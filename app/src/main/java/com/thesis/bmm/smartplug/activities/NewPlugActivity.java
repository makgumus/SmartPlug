package com.thesis.bmm.smartplug.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Plugs;

public class NewPlugActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner roomSpinner;
    private EditText plugName;
    private Button plugAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plug);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {
        roomSpinner = findViewById(R.id.spinner_room);
        plugName = findViewById(R.id.text_plugname);
        plugAddButton = findViewById(R.id.button_plugadd);
        initEvent();
    }

    private void initEvent() {
        plugAddButton.setOnClickListener(this);
    }

    private void addNewPlugatFirebase() //Firebase'de yeni bir priz açma
    {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("Plugs");
        String id = databaseReferencePlug.push().getKey();
        Plugs plug = new Plugs(id, plugName.getText().toString(), roomSpinner.getSelectedItem().toString(), "0", "false");
        databaseReferencePlug.child(id).setValue(plug);
    }

    @Override
    public void onClick(View view) {

        if (plugName.getText().length() > 0) {
            addNewPlugatFirebase();
            Toast.makeText(getApplicationContext(), "Yeni Priz Eklendi", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewPlugActivity.this);
        builder.setTitle("Hata");
        builder.setMessage("Lütfen Prizin Adını Giriniz!");
        String positiveText = "TAMAM";
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
