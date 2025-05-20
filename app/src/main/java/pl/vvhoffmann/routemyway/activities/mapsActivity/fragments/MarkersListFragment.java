package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.services.RouteOptimizationService;
import pl.vvhoffmann.routemyway.utils.MarkerUtils;

import com.google.android.gms.maps.model.Marker;

public class MarkersListFragment extends Fragment {
    private ListView listView;
    private Marker selectedMarker;

    private String[] items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Initialize components
        listView = view.findViewById(R.id.listView);
        Button btnRemoveMarker = view.findViewById(R.id.btnRemoveMarker);

        refreshList(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnRemoveMarker.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            view1.setSelected(true);
            selectedMarker = MarkerUtils.createMarkerFromString(parent.getItemAtPosition(position).toString());
            view1.setBackgroundColor(Color.parseColor(("#ADD8E6")));

            if (listView.getCheckedItemPosition() != ListView.INVALID_POSITION)
                btnRemoveMarker.setVisibility(View.VISIBLE);

        });

        btnRemoveMarker.setOnClickListener(v -> {
            String selectedItem = (String) listView.getItemAtPosition(listView.getCheckedItemPosition());
            selectedMarker = MarkerUtils.createMarkerFromString(selectedItem);
            Log.e("TAG", "onCreateView: " + selectedMarker.getPosition());
            removeMarker(selectedMarker);
            btnRemoveMarker.setVisibility(View.INVISIBLE);
            refreshList(listView);
        });
        return view;
    }

    private void refreshList(ListView listView) {
        items = new String[MarkersRepository.getSize() ];
        System.arraycopy(MarkersRepository.getMarkers()
                .stream()
                .map(Marker::getTitle)
                .toArray(String[]::new), 0, items, 0, items.length);
        // Set up an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }


    // Funkcja do usuwania ostatniego markera
    private void removeMarker(Marker selectedMarker) {
        if (!MarkersRepository.getMarkers().isEmpty() &&  MarkersRepository.containsMarker(selectedMarker)) {
            MarkersRepository.removeMarker(selectedMarker);
            Toast.makeText(requireContext(), "Marker usunięty", Toast.LENGTH_SHORT).show();

            if(RouteRepository.isRouteCalculated())
                RouteRepository.saveRoute(RouteOptimizationService.getOptimalRoute());


        } else {
            Toast.makeText(requireContext(), "Brak markerów do usunięcia", Toast.LENGTH_SHORT).show();
        }
    }
}
