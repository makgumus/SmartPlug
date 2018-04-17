package com.thesis.bmm.smartplug.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.app.MultiLanguage;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView signup,forget;
    AppCompatSpinner electricityuse;
    EditText editTextEmail, editTextPassword,editpasswordrepeature;
    CheckBox remember;
    ProgressBar progressBar;
    FirebaseUser user;
    String account;
    private FirebaseAuth mAuth;
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
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        editpasswordrepeature=findViewById(R.id.passwordrepeature);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        remember=findViewById(R.id.remember);
        forget=findViewById(R.id.forget);
        electricityuse=findViewById(R.id.electricityuse);
        progressBar =  findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        account=null;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setBackgroundResource(R.drawable.roundwhitebutton);
                login.setTextColor(getResources().getColor(R.color.pinklight));
                signup.setBackgroundResource(R.drawable.roundbutton);
                signup.setTextColor(getResources().getColor(R.color.white));
                userLogin();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editpasswordrepeature.setVisibility(View.VISIBLE);
                electricityuse.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                forget.setText(""+getResources().getString(R.string.accont));
                forget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        login.setVisibility(View.VISIBLE);
                        login.setBackgroundResource(R.drawable.roundwhitebutton);
                        login.setTextColor(getResources().getColor(R.color.pinklight));
                        signup.setBackgroundResource(R.drawable.roundbutton);
                        signup.setTextColor(getResources().getColor(R.color.white));
                        editpasswordrepeature.setVisibility(View.GONE);
                        electricityuse.setVisibility(View.GONE);
                        forget.setText(""+getResources().getString(R.string.forgetyourpassword));
                    }
                });
                login.setBackgroundResource(R.drawable.roundbutton);
                login.setTextColor(getResources().getColor(R.color.white));
                signup.setBackgroundResource(R.drawable.roundwhitebutton);
                signup.setTextColor(getResources().getColor(R.color.pinklight));
                registerUser();
            }
        });
    }
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordrepeature = editpasswordrepeature.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError(""+getResources().getString(R.string.emailrequired));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(""+getResources().getString(R.string.emailplease));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(""+getResources().getString(R.string.passwordrequired));
            editTextPassword.requestFocus();
            return;
        }

        if (passwordrepeature.isEmpty()) {
            editpasswordrepeature.setError(""+getResources().getString(R.string.passwordrequired));
            editpasswordrepeature.requestFocus();
            return;
        }
        if(password.equals(""+passwordrepeature))
        {

        }
        else{
            editpasswordrepeature.setError(""+getResources().getString(R.string.passworderror));
            editpasswordrepeature.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError(""+getResources().getString(R.string.passwordlen));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    SavePreferencesString("userID", ""+user.getUid());
                    DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference(""+user.getUid()).child("usertarife");
                    if(electricityuse.getSelectedItemPosition()==1) {
                        databaseReferencePlug.setValue("T1");
                    }
                    if(electricityuse.getSelectedItemPosition()==2) {
                        databaseReferencePlug.setValue("T2");
                    }
                    if(electricityuse.getSelectedItemPosition()==3) {
                        databaseReferencePlug.setValue("T3");
                    }
                    DatabaseReference databaseReferencePlug2 = FirebaseDatabase.getInstance().getReference(""+user.getUid()).child("account");
                    if(account!=null)
                        databaseReferencePlug2.setValue(""+account);

                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), ""+getResources().getString(R.string.registed), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), " "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError(""+getResources().getString(R.string.emailrequired));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(""+getResources().getString(R.string.emailplease));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(""+getResources().getString(R.string.passwordrequired));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(""+getResources().getString(R.string.passwordlen));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    SavePreferencesString("userID", ""+user.getUid());
                    DatabaseReference databaseReferencePlug2 = FirebaseDatabase.getInstance().getReference(""+user.getUid()).child("account");
                    if(account!=null)
                        databaseReferencePlug2.setValue(""+account);
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SavePreferencesString(String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String  datauserid= sharedPreferences.getString("userID", "Yok") ;
            DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference(""+datauserid).child("account");
            databaseReferencePlug.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if(value!=null) {
                        if (value.equals("on")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public void itemClicked(View v) {
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            account="on";
        }
        else {
            account="off";
        }
    }
}
