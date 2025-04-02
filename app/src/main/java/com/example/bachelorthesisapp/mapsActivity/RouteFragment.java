package com.example.bachelorthesisapp.mapsActivity;

import static java.util.Arrays.stream;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bachelorthesisapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

public class RouteFragment extends Fragment {

    private LinkedHashMap<LatLng, Marker> markers;
    private ArrayList<LatLng> points = new ArrayList<>();
    public static ArrayList<Marker> resultMarkers;
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


        markers = ((MapsActivity) requireActivity()).getMarkers();
        Log.i("Markers", " " + markers);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Marker currentPositionMarker = MapFragment.currentPositionMarker;
        points.add(currentPositionMarker.getPosition());
        points.addAll(markers.keySet());

        try {
            if (distanceMatrix == null)
                distanceMatrix = DirectionsHelper.getDistanceArray(points);

            ArrayList<LatLng> path = HeldKarpAlgorithm.getTSPSolution(points, distanceMatrix);
            Log.d("PathRoute", "Ścieżka: " + path);

            resultMarkers = new ArrayList<>();
            resultMarkers.add(currentPositionMarker);

            for (LatLng latLng : path)
                if(!latLng.equals(currentPositionMarker.getPosition()))
                    resultMarkers.add(markers.get(latLng));
            resultMarkers.add(currentPositionMarker);

            Log.i("PATHM " , " ResultMarkers: " + markers );
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
        String[] items = new String[resultMarkers.size()];

        for (int i = 0; i < resultMarkers.size(); i++) {
            items[i] = resultMarkers.get(i).getTitle();
        }
        // Set up an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }
}
