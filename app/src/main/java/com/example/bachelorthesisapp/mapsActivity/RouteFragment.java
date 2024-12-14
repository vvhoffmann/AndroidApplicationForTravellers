package com.example.bachelorthesisapp.mapsActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bachelorthesisapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteFragment extends Fragment {

    private DirectionsHelper directionsHelper;
    private ListView listView;
    private HashMap<Marker, LatLng> markers;
    private ArrayList<LatLng> points = new ArrayList<>();

    Button btnCalculate;
    Button btnShowMap;
    TextView tvTitle;
    TextView tvResult;
    TextView tvResultDescription;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_route, container, false);

        listView = view.findViewById(R.id.list_view);
        btnCalculate = view.findViewById(R.id.buttonCalculate);
        btnShowMap = view.findViewById(R.id.buttonShowMap);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvResult = view.findViewById(R.id.tvResultTitle);
        tvResultDescription = view.findViewById(R.id.tvResultDescription);


        markers = MapFragment.markers;
        if(markers.size() > 1)
            points.addAll(markers.values());

        if (markers.size() > 1) {
            btnCalculate.setEnabled(true);
            points.addAll(markers.values());
        }

        btnCalculate.setOnClickListener(v -> {
            tvResult.setVisibility(View.VISIBLE);
            tvResultDescription.setText("Wynik: ");
            btnShowMap.setVisibility(View.VISIBLE);
        });

        return view;
    }
}
