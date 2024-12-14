package com.example.bachelorthesisapp.mapsActivity;

import android.graphics.Color;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class MarkersListFragment extends Fragment {
    private ListView listView;
    private HashMap<Marker, LatLng> markers;
    private Marker currentPositionMarker;
    private TextView tvCurrentMarker;
    private GoogleMap mMap;
    private Marker selectedMarker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        markers = MapFragment.markers;

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Initialize components
        ListView listView = view.findViewById(R.id.listView);
        TextView tvCurrentMarker = view.findViewById(R.id.tvCurrentMarker);
        Button btnRemoveMarker = view.findViewById(R.id.btnRemoveMarker);

        currentPositionMarker = MapFragment.currentPositionMarker;
        tvCurrentMarker.setText("[ " + currentPositionMarker.getPosition().latitude + " , " +
                currentPositionMarker.getPosition().longitude + " ]");

        refreshList(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnRemoveMarker.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            view1.setSelected(true);
            selectedMarker = createMarkerFromString(parent.getItemAtPosition(position).toString());
            Log.e("TAG", "onCreateView: " + selectedMarker.getPosition());
            view1.setBackgroundColor(Color.parseColor(("#ADD8E6")));

            if (listView.getCheckedItemPosition() != ListView.INVALID_POSITION)
                btnRemoveMarker.setVisibility(View.VISIBLE);

        });


        btnRemoveMarker.setOnClickListener(v -> {
            String selectedItem = (String) listView.getItemAtPosition(listView.getCheckedItemPosition());
            selectedMarker = createMarkerFromString(selectedItem);
            Log.e("TAG", "onCreateView: " + selectedMarker.getPosition());
            removeMarker(selectedMarker);
            btnRemoveMarker.setVisibility(View.INVISIBLE);
            refreshList(listView);
        });
        return view;
    }

    private void refreshList(ListView listView) {
        // Sample data for the ListView
        String[] items = markers.keySet()
                .stream()
                .map(marker -> createStringFromMarker(marker)).toArray(String[]::new);

        // Set up an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }


    // Funkcja do usuwania ostatniego markera
    private void removeMarker(Marker selectedMarker) {
        if (!markers.isEmpty() && markers.containsKey(selectedMarker)) {
            markers.remove(selectedMarker); // Usunięcie markera z listy
            Toast.makeText(requireContext(), "Marker usunięty", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Brak markerów do usunięcia", Toast.LENGTH_SHORT).show();
        }
    }

    private String createStringFromMarker(Marker marker) {
        return  marker.getTitle() + "\n[ "
                + marker.getPosition().latitude + " , "
                + marker.getPosition().longitude + " ]";
    }

    private Marker createMarkerFromString(String text) {
        text = text.replaceAll("\\]", "");
        String[] parts = text.split("\\[");
        String[] coordinates = parts[1].trim().split(",");

        double lat = Double.parseDouble(coordinates[0].trim());
        double lng = Double.parseDouble(coordinates[1].trim());

        LatLng latLng = new LatLng(lat, lng);
        for (Map.Entry<Marker, LatLng> entry : markers.entrySet()) {
            if (entry.getValue().equals(latLng)) {
                return entry.getKey();
            }
        }
        return null;
    }
}