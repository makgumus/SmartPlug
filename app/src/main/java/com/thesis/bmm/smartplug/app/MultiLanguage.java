package com.thesis.bmm.smartplug.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;

/**
 * Created by merve on 3.4.2018.
 */

public class MultiLanguage extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static void setLocaleEn (Context context){
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
    public static void setLocaleTr (Context context){
        Locale locale = new Locale("");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
}
