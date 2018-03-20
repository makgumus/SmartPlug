package com.thesis.bmm.smartplug.services;

import android.os.AsyncTask;

import com.thesis.bmm.smartplug.model.ElectricityInterrupt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by MUHAMMED on 7.03.2018.
 */

public class GetInterruptContentAsyncTask extends AsyncTask<String, Void, String> {
    Document doc = null;
    String contentText = null;
    ElectricityInterrupt electricityInterrupt = new ElectricityInterrupt();

    public GetInterruptContentAsyncTask(ElectricityInterrupt interrupt) {
        this.electricityInterrupt = interrupt;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            doc = Jsoup.connect(strings[0]).get();
            Elements p = doc.select("p");
            String content = p.first().text().toString();
            electricityInterrupt.setExplain(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
