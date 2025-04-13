package pl.vvhoffmann.routemyway.services;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

public class MapService {
    private static GoogleMap map;
    private static LinkedList<MarkerOptions> markerOptionsList;

    public void addMarker(Marker marker) {
        MarkerOptions markerOptions = new MarkerOptions().position(marker.getPosition()).title(marker.getTitle());
        map.addMarker(markerOptions);
        markerOptionsList.add(markerOptions);
    }

    public static GoogleMap getMap() {
        return map;
    }

    public static void setMap(GoogleMap googleMap) {
        map = googleMap;
    }
}