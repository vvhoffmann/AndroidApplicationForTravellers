package pl.vvhoffmann.routemyway.services;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapService {
    private static GoogleMap map;

    public void addMarker(double latitude, double longitude, String title) {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title));
    }

    public static GoogleMap getMap() {
        return map;
    }

    public static void setMap(GoogleMap googleMap) {
        map = googleMap;
    }
}