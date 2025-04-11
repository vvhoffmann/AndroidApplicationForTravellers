package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

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

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;
import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.services.RouteOptimizationService;
import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;
import pl.vvhoffmann.routemyway.utils.PlacesUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;

public class RouteFragment extends Fragment {

    private ArrayList<LatLng> points = new ArrayList<>();
    public static ArrayList<Marker> resultMarkers;
    private double[][] distanceMatrix;
    private ArrayList<LatLng> resultPath = new ArrayList<>();
    private boolean isRouteCalculated = false;

    Button btnShowMap;
    Button btnEditPoints;
    TextView tvTitle;
    TextView tvResultTitle;
    TextView tvResultDescription;
    ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        initializeUIComponents(view);

        double minDistance = 0.0;

        if (distanceMatrix == null)
            distanceMatrix = PlacesUtils.getDistanceArray();

        RouteModel routeModel = RouteOptimizationService.getOptimalRoute();
        Log.d("PathRoute", "Scieżka: " + routeModel);

        minDistance = routeModel.getDistance();


        if(points != null && !points.isEmpty() && points.size() > 3 && resultMarkers != null && !resultMarkers.isEmpty()) {
            refreshList(listView);
            tvResultTitle.setVisibility(View.VISIBLE);
            tvResultDescription.setText(minDistance + " km");
            btnShowMap.setVisibility(View.VISIBLE);
            btnEditPoints.setVisibility(View.VISIBLE);
            isRouteCalculated = true;
        }

        btnShowMap.setOnClickListener(v -> ((MapsActivity) getActivity()).replaceFragment(new MapFragment()));

        return view;
    }

    private void initializeUIComponents(View view) {
        listView = view.findViewById(R.id.list_view);
        btnShowMap = view.findViewById(R.id.buttonShowMap);
        btnEditPoints = view.findViewById(R.id.btnEditPoints);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvResultTitle = view.findViewById(R.id.tvResultTitle);
        tvResultDescription = view.findViewById(R.id.tvResultDescription);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
