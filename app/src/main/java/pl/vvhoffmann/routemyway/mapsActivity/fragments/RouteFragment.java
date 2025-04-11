package pl.vvhoffmann.routemyway.mapsActivity.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.mapsActivity.DirectionsHelper;
import pl.vvhoffmann.routemyway.mapsActivity.MapsActivity;
import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class RouteFragment extends Fragment {

    private LinkedHashMap<LatLng, Marker> markers;
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

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        double minDistance = 0.0;
        Marker currentPositionMarker = MapFragment.currentPositionMarker;
        points.clear();
        points.add(currentPositionMarker.getPosition());
        points.addAll(markers.keySet());
        String message = "Points: " + Arrays.deepToString(points.toArray());
        Log.d("Points przeszlo", message);


        try {
            if (distanceMatrix == null)
                distanceMatrix = DirectionsHelper.getDistanceArray(points);

            ArrayList<LatLng> path = HeldKarpAlgorithm.getTSPSolution(points, distanceMatrix);
            Log.d("PathRoute", "Scieżka: " + path);

            //minDistance = HeldKarpAlgorithm.getFinalDistance(path, distanceMatrix);
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
