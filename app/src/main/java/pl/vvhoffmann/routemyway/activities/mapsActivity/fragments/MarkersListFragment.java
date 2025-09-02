package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.constants.Messages;
import pl.vvhoffmann.routemyway.services.ToastService;
import pl.vvhoffmann.routemyway.utils.MarkerUtils;
import static pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity.getMarkersRepository;

import com.google.android.gms.maps.model.Marker;

public class MarkersListFragment extends Fragment {
    private ListView listView;
    private int selectedPosition = ListView.INVALID_POSITION;
    private Marker selectedMarker;
    private Button btnRemoveMarker;
    private ArrayAdapter<String> adapter;
    private String[] items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        listView = view.findViewById(R.id.listView);
        btnRemoveMarker = view.findViewById(R.id.btnRemoveMarker);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnRemoveMarker.setVisibility(View.INVISIBLE);

        refreshList();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            selectedPosition = position; // zapamiętujemy wybraną pozycję
            selectedMarker = MarkerUtils.createMarkerFromString(parent.getItemAtPosition(position).toString());

            btnRemoveMarker.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged(); // odświeżamy widoki, żeby pokazać nowy kolor
        });

        btnRemoveMarker.setOnClickListener(v -> {
            if (selectedPosition != ListView.INVALID_POSITION) {
                String selectedItem = items[selectedPosition];
                selectedMarker = MarkerUtils.createMarkerFromString(selectedItem);
                removeMarker(selectedMarker);

                selectedPosition = ListView.INVALID_POSITION;
                btnRemoveMarker.setVisibility(View.INVISIBLE);
                refreshList();
            }
        });

        return view;
    }

    private void refreshList() {
        items = getMarkersRepository()
                .getMarkers()
                .stream()
                .map(Marker::getTitle)
                .toArray(String[]::new);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (position == selectedPosition)
                    view.setBackgroundColor(Color.parseColor(Constants.SELECTED_MARKER_COLOR));
                else
                    view.setBackgroundColor(Color.TRANSPARENT);

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    private void removeMarker(Marker selectedMarker) {
        if (!getMarkersRepository().getMarkers().isEmpty() && getMarkersRepository().containsMarker(selectedMarker)) {
            getMarkersRepository().removeMarker(selectedMarker);
            ToastService.showToast(Messages.MARKER_DELETED_MESSAGE, requireContext());
        } else {
            ToastService.showToast(Messages.NO_MORE_MARKERS_TO_DELETE_MESSAGE, requireContext());
        }
    }
}
