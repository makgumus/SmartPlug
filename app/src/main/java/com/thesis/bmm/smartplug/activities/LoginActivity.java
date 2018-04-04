package com.thesis.bmm.smartplug.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.app.MultiLanguage;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView register;
    Spinner electricityuse;
    EditText email,password,passwordrepeature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  datahighdil = sharedPreferences.getString("dil", "Yok") ;
        if(datahighdil.equals("Turkish")) {
            MultiLanguage.setLocaleTr(LoginActivity.this);
        }
        else if(datahighdil.equals("English"))
        {
            MultiLanguage.setLocaleEn(LoginActivity.this);
        }
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        passwordrepeature=findViewById(R.id.passwordrepeature);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        electricityuse=findViewById(R.id.electricityuse);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   ///Firebase kayıt yeri
                passwordrepeature.setVisibility(View.VISIBLE);
                electricityuse.setVisibility(View.VISIBLE);
                login.setText(""+getResources().getString(R.string.register));
                register.setVisibility(View.GONE);
            }
        });

    }
}
