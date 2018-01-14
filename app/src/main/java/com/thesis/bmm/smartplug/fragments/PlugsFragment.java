package com.thesis.bmm.smartplug.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.thesis.bmm.smartplug.R;
import com.thesis.bmm.smartplug.adapter.CustomAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlugsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlugsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlugsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG_NAME = "Priz";
    private static final String TAG_DESCRIPTION = "AkımDegeri";
    ListView list;
    ArrayList<HashMap<String, String>> newItemlist;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public PlugsFragment() {
        // Required empty public constructor
    }

    public static PlugsFragment newInstance(String param1, String param2) {
        PlugsFragment fragment = new PlugsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        View view = inflater.inflate(R.layout.fragment_plugs, container, false);
        list=view.findViewById(R.id.prizler);
        CustomAdapter cus = new CustomAdapter(getActivity(),newItemlist);
        list.setAdapter(cus);
        return inflater.inflate(R.layout.fragment_plugs, container, false);

    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
