package pl.vvhoffmann.routemyway.repositories;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getLatLngFromMarker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.utils.MarkerUtils;
import pl.vvhoffmann.routemyway.utils.PlacesUtils;

public class MarkersRepository {

    private static final MarkersRepository MARKERS_REPOSITORY = new MarkersRepository();
    private Marker currentPositionMarker = null;
    private LinkedList<Marker> markers = new LinkedList<>();
    private LinkedHashMap<LatLng,Marker> markersMap = new LinkedHashMap<>();
    private double[][] distances ;
    private int size = 1;
    private boolean areDistancesCalculated = false;

    private MarkersRepository() {}

    public static MarkersRepository getInstance() {
        return MARKERS_REPOSITORY;
    }

    public void addMarker(Marker marker) {
        markers.add(marker);
        markersMap.put(getLatLngFromMarker(marker),marker);
        size++;
    }

    public LinkedList<Marker> getMarkers() {
        return markers;
    }

    public Marker getMarkerByLatLng(LatLng latLng) {
        return markersMap.get(latLng);
    }

    public Marker getCurrentPositionMarker() {
        return currentPositionMarker;
    }

    public void setCurrentPositionMarker(Marker marker) {
        currentPositionMarker = marker;
        markers.addFirst(currentPositionMarker);
        markersMap.put(MarkerUtils.getLatLngFromMarker(currentPositionMarker),currentPositionMarker);
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
        markersMap.remove(getLatLngFromMarker(marker));
        size--;

        if(areDistancesCalculated)
            setDistances();
    }

    public double[][] getDistanceArray() {
        setDistances();
        return distances;
    }

    private void setDistances() {
        distances = PlacesUtils.getDistanceArray();
        areDistancesCalculated = true;
    }

    public LinkedList<LatLng> getLatLngList() {
        LinkedList<LatLng> latLngList = new LinkedList<>();
        for (Marker marker : markers) {
            latLngList.add(marker.getPosition());
        }
        return latLngList;
    }

    public int getSize() {
        return size;
    }

    public boolean areDistancesCalculated() {
        return areDistancesCalculated;
    }

    public boolean containsMarker(Marker selectedMarker) {
        return markersMap.containsKey(selectedMarker.getPosition());
    }

    public String getDescription() {
        String text =  "MarkersRepository{";
        for (Marker marker : markers) {
            text += "\n" + marker.getTitle() + " " + marker.getPosition();
        }
        text += "\n}";
        return text;
    }
}