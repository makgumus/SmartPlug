package com.thesis.bmm.smartplug.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.bmm.smartplug.LocationRequest;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.model.Locations;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {
    Spinner[] spn;
    private View view;
    private LocationRequest locationRequest;
    private FloatingActionButton locationAdd;
    private ArrayList districtList = new ArrayList();
    private ArrayList regionList = new ArrayList();
    private String city, county;
    private String firstRequestURL = "http://www.nufusune.com/" + city + "-ilceleri";
    private String secondRequestURL = "http://www.nufusune.com/" + county + "-mahalleleri-koyleri-" + city;
    private Spinner province, district, region;
    private ArrayAdapter<String> provinceSpinnerAdapter, districtSpinnerAdapter, regionSpinnerAdapter;
    private Switch interruptNotificationSwitch;
    private String[] cities = {"ADANA", "KONYA", "ADIYAMAN", "KÜTAHYA", "AFYONKARAHİSAR", "MALATYA", "AĞRI",
            "MANİSA", "AMASYA", "KAHRAMANMARAŞ", "ANKARA", "MARDİN", "ANTALYA", "MUĞLA", "ARTVİN", "MUŞ", "AYDIN", "NEVŞEHİR",
            "BALIKESİR", "NİĞDE", "BİLECİK", "ORDU", "BİNGÖL", "RİZE", "BİTLİS", "BOLU", "SAMSUN", "BURDUR", "SİİRT",
            "BURSA", "SİNOP", "ÇANAKKALE", "SİVAS", "ÇANKIRI", "TEKİRDAĞ", "ÇORUM", "TOKAT", "DENİZLİ", "TRABZON", "DİYARBAKIR",
            "TUNCELİ", "EDİRNE", "ŞANLIURFA", "ELAZIĞ", "UŞAK", "ERZİNCAN", "VAN", "ERZURUM", "YOZGAT", "ESKİŞEHİR", "ZONGULDAK",
            "GAZİANTEP", "AKSARAY", "GİRESUN", "BAYBURT", "GÜMÜŞHANE", "KARAMAN", "HAKKARİ", "KIRIKKALE", "HATAY", "BATMAN",
            "ISPARTA", "ŞIRNAK", "MERSİN", "BARTIN", "İSTANBUL", "ARDAHAN", "İZMİR", "IĞDIR", "KARS", "YALOVA", "KASTAMONU",
            "KARABÜK", "KAYSERİ", "KİLİS", "KIRKLARELİ", "OSMANİYE", "KIRŞEHİR", "DÜZCE", "KOCAELİ"};

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View views) {
        locationAdd = views.findViewById(R.id.button_adress_add);
        provinceSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cities);
        districtSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, districtList);
        regionSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, regionList);
        initEvent();
    }

    private void initEvent() {

        locationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRequest = new LocationRequest(getContext());
                locationRequest.selectAdressDialog(1, "null");
                //selectAdressDialog();
            }
        });
    }

    private void selectAdressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        int textViewCount = 4;
        TextView[] tvMessage = new TextView[textViewCount];
        for (int i = 0; i < textViewCount; i++) {
            tvMessage[i] = new TextView(getContext());
        }

        final Spinner[] spn = new Spinner[4];
        for (int i = 0; i < spn.length; i++) {
            spn[i] = new Spinner(getContext());
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
                StringRequest(firstRequestURL, districtList, spn[1], districtSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                county = spn[1].getSelectedItem().toString().toLowerCase();
                StringRequest(secondRequestURL, regionList, spn[2], regionSpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spn[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addNewLocationatFirebase();
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

    private void StringRequest(String URL, final ArrayList List, final Spinner spinner, final ArrayAdapter<String> adapter) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Elements link = doc.select("a");
                List.clear();
                for (int i = 0; i < link.size(); i++) {
                    if (i != 0 && i != 1 && i != 2) {
                        List.add(link.get(i).text());
                    }
                }
                adapter.notifyDataSetChanged();
                spinner.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}
