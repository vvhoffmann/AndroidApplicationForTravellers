package com.example.bachelorthesisapp.mapsActivity;

import android.os.Bundle;
import android.util.Log;
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

    private double[][] distanceMatrix;

    Button btnCalculate;
    Button btnShowMap;
    Button btnEditPoints;
    TextView tvTitle;
    TextView tvResultTitle;
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
        btnEditPoints = view.findViewById(R.id.btnEditPoints);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvResultTitle = view.findViewById(R.id.tvResultTitle);
        tvResultDescription = view.findViewById(R.id.tvResultDescription);

        markers.put(MarkersListFragment.currentPositionMarker, MarkersListFragment.currentPositionMarker.getPosition());
        markers = MapFragment.markers;

        if(markers.size() > 1)
            btnCalculate.setEnabled(true);


        btnCalculate.setOnClickListener(v -> {
            points.addAll(markers.values());

            new Thread(() -> {
                try {
                    distanceMatrix = DirectionsHelper.getDistanceArray(points);

                    for (int i = 0; i < points.size(); i++) {
                        for (int j = 0; j < points.size(); j++) {
                            Log.d("DistanceMatrix", "Dystans [" + i + "][" + j + "]: " + distanceMatrix[i][j] + " km");
                        }
                    }
                } catch (Exception e) {
                    Log.e("Directions", "Błąd: " + e.getMessage());
                }
            }).start();

            tvResultTitle.setVisibility(View.VISIBLE);
            tvResultDescription.setText("Wynik: ");
            btnShowMap.setVisibility(View.VISIBLE);
            btnEditPoints.setVisibility(View.VISIBLE);
        });

        return view;
    }
}
