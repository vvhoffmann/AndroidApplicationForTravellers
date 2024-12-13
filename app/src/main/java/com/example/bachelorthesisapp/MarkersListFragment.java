package com.example.bachelorthesisapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MarkersListFragment extends Fragment {
    private ListView listView ;
    private HashMap<Marker, LatLng> markers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        markers = MapFragment.markers;

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Find the ListView
        ListView listView = view.findViewById(R.id.listView);


        AtomicInteger counter = new AtomicInteger(1);
        // Sample data for the ListView
        String[] items = markers.keySet()
                .stream()
                .map(marker -> counter.getAndIncrement() + ".  " + marker.getTitle() + "\n [ "
                        + marker.getPosition().latitude + " , "
                        + marker.getPosition().longitude + " ]").toArray(String[]::new);

//        String[] items = Stream.concat(
//                Stream.of(counter.getAndIncrement() + ".  " + currentPositionMarker.getTitle() + "\n [ "
//                        + currentPositionMarker.getPosition().latitude + " , "
//                        + currentPositionMarker.getPosition().longitude + " ]"),
//                markers.keySet()
//                        .stream()
//                        .map(marker -> counter.getAndIncrement() + ".  " + marker.getTitle() + "\n [ "
//                                + marker.getPosition().latitude + " , "
//                                + marker.getPosition().longitude + " ]")
//        ).toArray(String[]::new);

        // Set up an adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);


        return view;
    }
}
