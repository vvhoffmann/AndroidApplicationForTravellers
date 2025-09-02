package pl.vvhoffmann.routemyway.models;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getMarkerByLatLng;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.services.RouteOptimizationService;
import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;
import pl.vvhoffmann.routemyway.utils.TSPAlgorithm;
import pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm.QuasiOptimizationAlgorithm;

public class RouteModel {
    private final List<Marker> markers;
    private int size;
    private double distance = 0;

    public List<Marker> getMarkers() {
        return markers;
    }

    public RouteModel(List<LatLng> latLngList) {
        markers = new LinkedList<>();
        for (LatLng latLng : latLngList)
            markers.add(getMarkerByLatLng(latLng));
        size = markers.size();
        setDistance();
    }

    private void setDistance() {
        distance = RouteOptimizationService.usedAlgorithm == TSPAlgorithm.HELD_KARP ?
                HeldKarpAlgorithm.getDistance() : QuasiOptimizationAlgorithm.getDistance();
    }

    public double getDistance() {
        return distance;
    }

    public int getSize() {
        return size;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Route{markers= ");
        for (Marker marker : markers)
            result.append(marker.getTitle()).append(", [").append(marker.getPosition()).append("] ");
        return result.toString();
    }
}