package com.thesis.bmm.smartplug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    Spinner[] spn;
    private ArrayList districtList;
    private ArrayList regionList;
    private ArrayAdapter<String> provinceSpinnerAdapter, districtSpinnerAdapter, regionSpinnerAdapter;
    private String city, county;
    private String firstRequestURL;
    private String secondRequestURL;
    private String[] cities = {"İSTANBUL", "SAKARYA", "ADANA", "KONYA", "ADIYAMAN", "KÜTAHYA", "AFYONKARAHİSAR", "MALATYA", "AĞRI",
            "MANİSA", "AMASYA", "KAHRAMANMARAŞ", "ANKARA", "MARDİN", "ANTALYA", "MUĞLA", "ARTVİN", "MUŞ", "AYDIN", "NEVŞEHİR",
            "BALIKESİR", "NİĞDE", "BİLECİK", "ORDU", "BİNGÖL", "RİZE", "BİTLİS", "BOLU", "SAMSUN", "BURDUR", "SİİRT",
            "BURSA", "SİNOP", "ÇANAKKALE", "SİVAS", "ÇANKIRI", "TEKİRDAĞ", "ÇORUM", "TOKAT", "DENİZLİ", "TRABZON", "DİYARBAKIR",
            "TUNCELİ", "EDİRNE", "ŞANLIURFA", "ELAZIĞ", "UŞAK", "ERZİNCAN", "VAN", "ERZURUM", "YOZGAT", "ESKİŞEHİR", "ZONGULDAK",
            "GAZİANTEP", "AKSARAY", "GİRESUN", "BAYBURT", "GÜMÜŞHANE", "KARAMAN", "HAKKARİ", "KIRIKKALE", "HATAY", "BATMAN",
            "ISPARTA", "ŞIRNAK", "MERSİN", "BARTIN", "ARDAHAN", "İZMİR", "IĞDIR", "KARS", "YALOVA", "KASTAMONU",
            "KARABÜK", "KAYSERİ", "KİLİS", "KIRKLARELİ", "OSMANİYE", "KIRŞEHİR", "DÜZCE", "KOCAELİ"};

    public LocationRequest(Context context) {
        this.context = context;
    }

    //int status=1  >> AddLocation
    //int status=0  >> UpdateLocation
    public void selectAdressDialog(final int status, final String location_id) {
        regionList = new ArrayList();
        districtList = new ArrayList();
        provinceSpinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cities);
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

        tvMessage[0].setText("Şehir Şeçiniz");
        tvMessage[1].setText("İlçe Şeçiniz");
        tvMessage[2].setText("Mahalle Şeçiniz");

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


        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        spn[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = spn[0].getSelectedItem().toString().toLowerCase();
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
                county = spn[1].getSelectedItem().toString().toLowerCase();
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
                if (status == 1) {
                    //     addNewLocationatFirebase();
                } else {
                    //   updateLocationatFirebase(location_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addNewLocationatFirebase() {
        DatabaseReference databaseReferencePlug = FirebaseDatabase.getInstance().getReference("Locations");
        String id = databaseReferencePlug.push().getKey();
        Locations location = new Locations(id, spn[0].getSelectedItem().toString(), spn[1].getSelectedItem().toString(), spn[2].getSelectedItem().toString(), false);
        databaseReferencePlug.child(id).setValue(location);
    }

    private void updateLocationatFirebase(String id) {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Locations").child(id);
        Locations location = new Locations(id, spn[0].getSelectedItem().toString(), spn[1].getSelectedItem().toString(), spn[2].getSelectedItem().toString(), false);
        dr.setValue(location);
    }

    public void deleteLocation(String id) {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Locations").child(id);
        dr.removeValue();
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
                for (int i = 0; i < rows.size(); i++) {
                    districtList.add(rows.get(i).text());

                }
                districtSpinnerAdapter.notifyDataSetChanged();
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
                    regionList.add(rows.get(i).text());

                }
                regionSpinnerAdapter.notifyDataSetChanged();
                spinner.setAdapter(regionSpinnerAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private String convertToCharacter(String text) {
        String convertedText = "";
        for (int i = 0; i < text.length(); i++) {
            switch (text.charAt(i)) {
                //burada kelimeyi tek tek dolasıyoruz
                //eğer ğ ı gibi kelime görürse bunu geçici kelimeye düzelterek ekliyor
                case 'ğ':
                    convertedText += 'g';
                    break;
                case 'ı':
                    convertedText += 'i';
                    break;
                case 'ş':
                    convertedText += 's';
                    break;
                case 'ü':
                    convertedText += 'u';
                    break;
                case 'ö':
                    convertedText += 'o';
                    break;
                case 'ç':
                    convertedText += 'c';
                    break;
                default:
                    convertedText += text.charAt(i);
            }
        }
        return convertedText;
    }

}
