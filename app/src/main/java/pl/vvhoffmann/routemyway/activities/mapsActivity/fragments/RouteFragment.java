package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import android.os.Bundle;
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
import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.services.RouteOptimizationService;


public class RouteFragment extends Fragment {
    Button btnShowMap;
    Button btnEditPoints;
    TextView tvTitle;
    TextView tvResultTitle;
    TextView tvResultDescription;
    ListView listView;

    RouteModel routeModel ;

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

        double minDistance;

        if(MarkersRepository.getInstance().getSize() > Constants.MINIMAL_MARKERS_COUNT_TO_PERFORM_ROUTE_OPTIMIZATION) {
            routeModel = RouteOptimizationService.getOptimalRoute();
            minDistance = routeModel.getDistance();
            refreshList(listView);

            tvResultTitle.setVisibility(View.VISIBLE);
            tvResultDescription.setVisibility(View.VISIBLE);
            tvResultDescription.setText(roundToTwoDigitsAfterComma(minDistance) + " km");
            btnShowMap.setVisibility(View.VISIBLE);
            btnEditPoints.setVisibility(View.VISIBLE);
        }
        else
            RouteRepository.getInstance().resetRoute();

        btnShowMap.setOnClickListener(v -> ((MapsActivity) requireActivity()).replaceFragment(MapsActivity.getMapFragment()));
        btnEditPoints.setOnClickListener(v -> ((MapsActivity) requireActivity()).replaceFragment(MapsActivity.getMarkersListFragment()));
        
        return view;
    }

    private static double roundToTwoDigitsAfterComma(double minDistance) {
        return Math.round(minDistance * 100.0) / 100.0;
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
        if(routeModel == null) {
            return;
        }
        int size = routeModel.getSize();

        String[] items = new String[size];

        for (int i = 0; i < size; i++)
            items[i] = routeModel.getMarkers().get(i).getTitle();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }
}