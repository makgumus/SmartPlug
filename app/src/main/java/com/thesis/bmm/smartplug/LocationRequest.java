package com.thesis.bmm.smartplug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.model.Locations;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by MUHAMMED on 13.03.2018.
 */

public class LocationRequest {
    Context context;
    private ArrayList provinceList, districtList, regionList;
    private ArrayAdapter<String> districtSpinnerAdapter, regionSpinnerAdapter, provinceSpinnerAdapter;
    private String city, county;
    private String firstRequestURL, secondRequestURL, provinceURL = "http://www.nufusune.com/ilceler";

    public LocationRequest(Context context) {
        this.context = context;
    }

    public LocationRequest() {
    }

    //int status=1  >> AddLocation
    //int status=0  >> UpdateLocation
    public void selectAdressDialog(final int status, final String location_id) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        final String firebaseUserId = firebaseUserInformation.getFirebaseUserId();
        regionList = new ArrayList();
        districtList = new ArrayList();
        provinceList = new ArrayList();
        provinceSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, provinceList);
        districtSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, districtList);
        regionSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, regionList);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        int textViewCount = 4;
        TextView[] tvMessage = new TextView[textViewCount];
        for (int i = 0; i < textViewCount; i++) {
            tvMessage[i] = new TextView(context);
        }
        final Spinner[] spn = new Spinner[4];
        for (int i = 0; i < spn.length; i++) {
            spn[i] = new Spinner(context);
        }
        StringProvinceRequest(provinceURL, spn[0]);
        tvMessage[0].setText("" + context.getResources().getString(R.string.chooseyourcity));
        tvMessage[1].setText("" + context.getResources().getString(R.string.chooseyourdistrict));
        tvMessage[2].setText("" + context.getResources().getString(R.string.chooseyourneighborhood));
        for (int i = 0; i < 3; i++) {
            tvMessage[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        }
        spn[0].setAdapter(provinceSpinnerAdapter);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < 3; i++) {
            layout.addView(tvMessage[i]);
            layout.addView(spn[i]);
        }
        layout.setPadding(50, 40, 50, 10);
        builder.setView(layout);


        builder.setNegativeButton("" + context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("" + context.getResources().getString(R.string.okey), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (status == 1) {
                    addNewLocationatFirebase(spn[0].getSelectedItem().toString(), spn[1].getSelectedItem().toString(), spn[2].getSelectedItem().toString(), firebaseUserId);
                } else {
                    updateLocationatFirebase(location_id, spn[0].getSelectedItem().toString(), spn[1].getSelectedItem().toString(), spn[2].getSelectedItem().toString(), false);
                }
            }
        });
        builder.create().show();
        spn[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = spn[0].getSelectedItem().toString();
                city = convertToCharacter(city);
                firstRequestURL = "http://www.nufusune.com/" + city + "-ilceleri";
                StringDistrictRequest(firstRequestURL, spn[1]);
                //StringRequest(firstRequestURL,spn[1],districtList,districtSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                county = spn[1].getSelectedItem().toString();
                county = convertToCharacter(county);
                secondRequestURL = "http://www.nufusune.com/" + county + "-mahalleleri-koyleri-" + city;
                StringRegionRequest(secondRequestURL, spn[2]);
                //  StringRequest(secondRequestURL,spn[2],regionList,regionSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spn[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addNewLocationatFirebase(String city, String district, String region, String userId) {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("" + userId).child("Locations");
        String id = databaseReferencePlug.push().getKey();
        Locations location = new Locations(id, city, district, region, false);
        databaseReferencePlug.child(id).setValue(location);
    }

    public void updateLocationatFirebase(String id, String city, String district, String region, Boolean status) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Locations").child(id);
        Locations location = new Locations(id, city, district, region, status);
        dr.setValue(location);
    }

    public void deleteLocation(String id) {
        FirebaseUserInformation firebaseUserInformation = new FirebaseUserInformation(context);
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("" + firebaseUserInformation.getFirebaseUserId()).child("Locations").child(id);
        dr.removeValue();
    }

    private void StringProvinceRequest(String URL, final Spinner spinner) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Elements tables = doc.select("table");
                Elements rows = tables.get(1).select("a");
                provinceList.clear();
                Log.i("birinci", rows.get(0).text());
                for (int i = 0; i < rows.size(); i++) {
                    provinceList.add(rows.get(i).text().substring(0, rows.get(i).text().length() - 9));
                    provinceSpinnerAdapter.notifyDataSetChanged();
                }
                spinner.setAdapter(provinceSpinnerAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void StringDistrictRequest(String URL, final Spinner spinner) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Elements tables = doc.select("table");
                Elements rows = tables.get(1).select("a");
                districtList.clear();
                Log.i("birinci", rows.get(0).text());
                for (int i = 0; i < rows.size(); i++) {
                    districtList.add(rows.get(i).text());
                    districtSpinnerAdapter.notifyDataSetChanged();
                }
                spinner.setAdapter(districtSpinnerAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void StringRegionRequest(String URL, final Spinner spinner) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Elements classes = doc.getElementsByClass("custom-counter");
                Elements rows = classes.first().select("a");
                regionList.clear();
                for (int i = 0; i < rows.size(); i++) {
                    regionList.add(rows.get(i).text().substring(0, rows.get(i).text().length() - 10));
                    regionSpinnerAdapter.notifyDataSetChanged();
                }
                spinner.setAdapter(regionSpinnerAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    public String convertToCharacter(String text) {
        text = text.toLowerCase();
        String[] a = {"ı", "ü", "ö", "ç", "ş", "ğ"};
        String[] b = {"i", "u", "o", "c", "s", "g"};
        for (int i = 0; i < a.length; i++) {
            text = text.replaceAll(a[i] + "", b[i] + "");
        }
        return text;
    }

}