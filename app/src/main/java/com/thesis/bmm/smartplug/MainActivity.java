package com.thesis.bmm.smartplug;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.thesis.bmm.smartplug.adapter.CustomAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ListView list;
    ArrayList<HashMap<String, String>> newItemlist = new ArrayList<HashMap<String, String>>();
    private static final String TAG_NAME = "Priz";
    private static final String TAG_DESCRIPTION = "AkımDegeri";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatButton fab = (AppCompatButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent=new Intent(MainActivity.this,NewPlugActivity.class);
                startActivity(intent);
            }
        });
        newItemlist = new ArrayList<HashMap<String, String>>();
        String name="Priz1";
        String name2="Priz2";
        String description="Akım Degeri";
        String description2="Akım Degeri2";

        HashMap<String, String> map = new HashMap<String, String>();  //Daha sonra veri tabanından gelicek.
        map.put(TAG_NAME, name);
        map.put(TAG_DESCRIPTION, description);
        newItemlist.add(map);

        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put(TAG_NAME, name2);
        map2.put(TAG_DESCRIPTION, description2);
        newItemlist.add(map2);

        list=(ListView)findViewById(R.id.prizler);
        CustomAdapter cus = new CustomAdapter(MainActivity.this,newItemlist);
        list.setAdapter(cus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
