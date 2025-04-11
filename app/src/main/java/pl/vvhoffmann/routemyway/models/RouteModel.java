package pl.vvhoffmann.routemyway.models;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getMarkerByLatLng;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.utils.PlacesUtils;

public class RouteModel {
    private final LinkedList<Marker> markers;
    private double distance = 0;
    private double duration = 0;

    // Getters and Setters
    public LinkedList<Marker> getMarkers() {
        return markers;
    }

    public RouteModel(List<Marker> markers) {
        this.markers = (LinkedList<Marker>) markers;
    }

    public RouteModel(LinkedList<LatLng> latLngList) {
        markers = new LinkedList<>();
        for (LatLng latLng : latLngList)
            markers.add(getMarkerByLatLng(latLng));
        setDistance();
    }

    private void setDistance() {
        for (int i = 0; i < markers.size() - 1; i++) {
            LatLng start = markers.get(i).getPosition();
            LatLng end = markers.get(i + 1).getPosition();
            try {
                distance += PlacesUtils.getWalkingRoute(start, end);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "markers=" + markers + '}';
    }
}