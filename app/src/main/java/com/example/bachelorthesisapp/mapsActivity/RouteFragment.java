package com.example.bachelorthesisapp.mapsActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private HashMap<LatLng, Marker> markers = null;

    private ArrayList<LatLng> points = new ArrayList<>();

    private double[][] distanceMatrix = null;
    private ArrayList<LatLng> resultPath = new ArrayList<>();
    private boolean isRouteCalculated = false;

    Button btnShowMap;
    Button btnEditPoints;
    TextView tvTitle;
    TextView tvResultTitle;
    TextView tvResultDescription;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        listView = view.findViewById(R.id.list_view);
        btnShowMap = view.findViewById(R.id.buttonShowMap);
        btnEditPoints = view.findViewById(R.id.btnEditPoints);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvResultTitle = view.findViewById(R.id.tvResultTitle);
        tvResultDescription = view.findViewById(R.id.tvResultDescription);


        if (markers == null)
            markers = MapFragment.markers;

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        points.addAll(markers.keySet());

        try {
            if (distanceMatrix == null)
                distanceMatrix = DirectionsHelper.getDistanceArray(points);

            for (int i = 0; i < points.size(); i++) {
                for (int j = 0; j < points.size(); j++) {
                    Log.d("DistanceMatrix", "Dystans [" + i + "][" + j + "]: " + distanceMatrix[i][j] + " km");
                }
            }

            ArrayList<LatLng> path = HeldKarpAlgorithm.getTSPSolution(points, distanceMatrix);
            Log.d("Path", "Ścieżka: " + path);

            ArrayList<Marker> resultMarkers = new ArrayList<>();
            for (LatLng latLng : path) {
                for (LatLng originalLatLng : markers.keySet()) {
                    if (originalLatLng.equals(latLng)) {
                        resultMarkers.add(markers.get(originalLatLng));
                        break;
                    }
                }
            }

            for (Marker marker : resultMarkers) {
                marker.showInfoWindow();
            }

        } catch (Exception e) {
            Log.e("Directions", "Błąd: " + e.getMessage());
        }


        refreshList(listView);
        tvResultTitle.setVisibility(View.VISIBLE);
        tvResultDescription.setText("Wynik: ");
        btnShowMap.setVisibility(View.VISIBLE);
        btnEditPoints.setVisibility(View.VISIBLE);
        isRouteCalculated = true;


        btnShowMap.setOnClickListener(v -> ((MapsActivity) getActivity()).replaceFragment(new MapFragment()));

        return view;
    }

    private void refreshList(ListView listView) {
        // Sample data for the ListView
        String[] items = markers.keySet()
                .stream()
                .map(latLng -> createStringFromMarker(markers.get(latLng))).toArray(String[]::new);

        // Set up an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    private String createStringFromMarker(Marker marker) {
        return marker.getTitle() + "\n[ "
                + marker.getPosition().latitude + " , "
                + marker.getPosition().longitude + " ]";
    }

    private Marker createMarkerFromString(String text) {
        text = text.replaceAll("\\]", "");
        String[] parts = text.split("\\[");
        String[] coordinates = parts[1].trim().split(",");

        double lat = Double.parseDouble(coordinates[0].trim());
        double lng = Double.parseDouble(coordinates[1].trim());

        LatLng position = new LatLng(lat, lng);
        for (LatLng latLng : markers.keySet()) {
            if (latLng.equals(position)) {
                return markers.get(latLng);
            }
        }
        return null;
    }
}
