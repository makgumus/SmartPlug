package com.thesis.bmm.smartplug;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesis.bmm.smartplug.model.ElectricityInterrupt;
import com.thesis.bmm.smartplug.services.GetInterruptContentAsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MUHAMMED on 12.03.2018.
 */

public class InterruptRequest {
    public ArrayList<ElectricityInterrupt> electricityInterruptList = new ArrayList<>();
    Context context;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private String province, county, neighborhood;
    private String URL = "https://guncelkesintiler.com/" + province.toLowerCase() + "/elektrik-kesintisi/";
    private String homeURL = "https://guncelkesintiler.com";

    public InterruptRequest(Context context, String province, String district, String region) {
        this.context = context;
        this.province = province;
        this.county = district;
        this.neighborhood = region;
    }

    public void request() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Document document = Jsoup.parse(response);
                    if (document != null) {
                        Elements table = document.select("table");
                        Elements rows = table.first().select("tbody>tr");
                        for (int i = 0; i < rows.size(); i++) {
                            ElectricityInterrupt electricityInterrupt = new ElectricityInterrupt();
                            Element row = rows.get(i);
                            Elements content = row.select("a");
                            String contentLink = content.first().attr("href");
                            String header = content.select("h6").text().toString();
                            String date = header.split(" ")[0] + "/" + convertMonth(header.split(" ")[1]) + "/" + header.split(" ")[2];
                            String district = header.split(" ")[3];
                            String region = row.select("p").text().toString();
                            if (dateTimeNowToString() == date && district.toLowerCase() == county.toLowerCase() && region.toLowerCase().contains(neighborhood.toLowerCase().split(" ")[0])) {// neighborhood.substring(0,(neighborhood.length())-4) Mah. kısmını çıkartıyor
                                electricityInterrupt.setDate(convertToDate(date));
                                electricityInterrupt.setProvince(province);
                                electricityInterrupt.setDistrict(district);
                                electricityInterrupt.setRegions(region);
                                electricityInterrupt.setContentLink(homeURL + contentLink);
                                electricityInterruptList.add(electricityInterrupt);
                            }
                        }
                        for (int i = 0; i < electricityInterruptList.size(); i++) {
                            new GetInterruptContentAsyncTask(electricityInterruptList.get(i)).execute(electricityInterruptList.get(i).getContentLink());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }


    public int getInterruptSize() {
        return electricityInterruptList.size();
    }

    private String convertMonth(String month) {
        String convertedtoMonth = null;
        switch (month) {
            case "Ocak":
                convertedtoMonth = "01";
                break;
            case "Şubat":
                convertedtoMonth = "02";
                break;
            case "Mart":
                convertedtoMonth = "03";
                break;
            case "Nisan":
                convertedtoMonth = "04";
                break;
            case "Mayıs":
                convertedtoMonth = "05";
                break;
            case "Haziran":
                convertedtoMonth = "06";
                break;
            case "Temmuz":
                convertedtoMonth = "07";
                break;
            case "Ağustos":
                convertedtoMonth = "08";
                break;
            case "Eylül":
                convertedtoMonth = "09";
                break;
            case "Ekim":
                convertedtoMonth = "10";
                break;
            case "Kasım":
                convertedtoMonth = "11";
                break;
            case "Aralık":
                convertedtoMonth = "12";
                break;
            default:
                break;
        }
        return convertedtoMonth;
    }

    private Date convertToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public String dateTimeNowToString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date now = new Date();
        final String dateTimeNow = df.format(now);
        return dateTimeNow;
    }

}
