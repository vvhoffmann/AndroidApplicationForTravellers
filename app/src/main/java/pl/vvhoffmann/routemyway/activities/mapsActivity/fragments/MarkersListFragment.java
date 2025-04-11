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
import pl.vvhoffmann.routemyway.utils.MarkerUtils;

import com.google.android.gms.maps.model.Marker;


public class MarkersListFragment extends Fragment {
    private ListView listView;
    private Button btnRemoveMarker;
    private Marker selectedMarker;

    private View selectedView;
    ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        refreshList(listView);

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initializeUIComponents(view);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnRemoveMarker.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            view1.setSelected(true);

            view1.setBackgroundColor(Color.parseColor(("#ADD8E6")));
            selectedView = view1;

            if (listView.getCheckedItemPosition() != ListView.INVALID_POSITION)
                btnRemoveMarker.setVisibility(View.VISIBLE);

            btnRemoveMarker.setOnClickListener(v -> {
                String selectedItem = (String) listView.getItemAtPosition(listView.getCheckedItemPosition());

                selectedMarker = MarkerUtils.createMarkerFromString(selectedItem);

                removeMarker(selectedMarker);
                btnRemoveMarker.setVisibility(View.INVISIBLE);
            });
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList(listView);
    }

    private void initializeUIComponents(View view) {
        // Initialize components
        listView = view.findViewById(R.id.listView);
        btnRemoveMarker = view.findViewById(R.id.btnRemoveMarker);
    }

    private void refreshList(ListView listView) {

        String[] items = new String[MarkersRepository.getSize()+1];
        System.arraycopy(MarkersRepository.getMarkers()
                .stream()
                .map(MarkerUtils::createMarkerDescription)
                .toArray(String[]::new), 0, items, 0, items.length);
        // Set up an adapter
        if(listView != null){
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);
        }

    }


    // Funkcja do usuwania ostatniego markera
    private void removeMarker(Marker selectedMarker) {
        if (MarkersRepository.getSize() > 0 && MarkersRepository.containsMarker(selectedMarker)) {
            MarkersRepository.removeMarker(selectedMarker); // Usunięcie markera z listy
            Toast.makeText(requireContext(), "Marker usunięty", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Brak markerów do usunięcia", Toast.LENGTH_SHORT).show();
        }
    }
}
