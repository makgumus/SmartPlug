package com.thesis.bmm.smartplug.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.ViewPagerAdapter;
import com.thesis.bmm.smartplug.app.MultiLanguage;
import com.thesis.bmm.smartplug.fragments.NotificationFragment;
import com.thesis.bmm.smartplug.fragments.PlugsFragment;
import com.thesis.bmm.smartplug.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private TabLayout tabLayout = null;
    public ViewPager vpFragments = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  datahighdil = sharedPreferences.getString("dil", "Yok") ;
        if(datahighdil.equals("Turkish")) {
            MultiLanguage.setLocaleTr(MainActivity.this);
        }
        else if(datahighdil.equals("English"))
        {
            MultiLanguage.setLocaleEn(MainActivity.this);
        }
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.activity_main_tablayout);
        vpFragments = findViewById(R.id.activity_main_vpFragments);
        initEvent();
    }

    private void initEvent() {
        tabLayout.setupWithViewPager(vpFragments);
        tabLayout.addOnTabSelectedListener(this);
        //tabLayout.setb
        ViewPagerAdapter viewpagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        PlugsFragment plugsFragment = new PlugsFragment();
        NotificationFragment notificationFragment = new NotificationFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        viewpagerAdapter.addFragment(plugsFragment, getResources().getString(R.string.plugs)+"");
        viewpagerAdapter.addFragment(notificationFragment, getResources().getString(R.string.notification)+"");
        viewpagerAdapter.addFragment(settingsFragment, getResources().getString(R.string.settings)+"");
        vpFragments.setAdapter(viewpagerAdapter);
        vpFragments.addOnPageChangeListener(this);
        vpFragments.setCurrentItem(0);
    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
