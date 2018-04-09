package com.thesis.bmm.smartplug.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.app.MultiLanguage;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView signup;
    AppCompatSpinner electricityuse;
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
        signup=findViewById(R.id.signup);
        electricityuse=findViewById(R.id.electricityuse);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setBackgroundResource(R.drawable.roundbutton);
                signup.setBackgroundResource(R.drawable.roundwhitebutton);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   ///Firebase kayıt yeri
                passwordrepeature.setVisibility(View.VISIBLE);
                electricityuse.setVisibility(View.VISIBLE);
                login.setBackgroundResource(R.drawable.roundwhitebutton);
                signup.setBackgroundResource(R.drawable.roundbutton);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
