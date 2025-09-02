package pl.vvhoffmann.routemyway.repositories;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getLatLngFromMarker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.utils.MarkerUtils;
import pl.vvhoffmann.routemyway.utils.RouteUtils;

public class MarkersRepository implements IMarkersRepository {

    private static IMarkersRepository MARKERS_REPOSITORY = new MarkersRepository();
    private Marker currentPositionMarker = null;
    private LinkedList<Marker> markers = new LinkedList<>();
    private LinkedHashMap<LatLng,Marker> markersMap = new LinkedHashMap<>();
    private double[][] distances ;
    private int size = 1;
    private boolean areDistancesCalculated = false;

    private MarkersRepository() {}

    public static IMarkersRepository getInstance() {
        return MARKERS_REPOSITORY;
    }

    public static void setInstanceForTests(IMarkersRepository instance) {
        MARKERS_REPOSITORY = instance;
    }

    @Override
    public void addMarker(Marker marker) {
        markers.add(marker);
        markersMap.put(getLatLngFromMarker(marker),marker);
        size++;
    }

    @Override
    public LinkedList<Marker> getMarkers() {
        return markers;
    }

    @Override
    public Marker getMarkerByLatLng(LatLng latLng) {
        return markersMap.get(latLng);
    }

    @Override
    public Marker getCurrentPositionMarker() {
        return currentPositionMarker;
    }

    @Override
    public void setCurrentPositionMarker(Marker marker) {
        currentPositionMarker = marker;
        markers.addFirst(currentPositionMarker);
        markersMap.put(MarkerUtils.getLatLngFromMarker(currentPositionMarker),currentPositionMarker);
    }

    @Override
    public void removeMarker(Marker marker) {
        markers.remove(marker);
        markersMap.remove(getLatLngFromMarker(marker));
        size--;

        if(areDistancesCalculated)
            setDistances();
    }

    @Override
    public double[][] getDistanceArray() {
        setDistances();
        return distances;
    }

    private void setDistances() {
        distances = RouteUtils.getDistanceArray();
        areDistancesCalculated = true;
    }

    @Override
    public LinkedList<LatLng> getLatLngList() {
        LinkedList<LatLng> latLngList = new LinkedList<>();
        for (Marker marker : markers) {
            latLngList.add(marker.getPosition());
        }
        return latLngList;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean areDistancesCalculated() {
        return areDistancesCalculated;
    }

    @Override
    public boolean containsMarker(Marker selectedMarker) {
        return markersMap.containsKey(selectedMarker.getPosition());
    }

    @Override
    public String getDescription() {
        String text =  "MarkersRepository{";
        for (Marker marker : markers) {
            text += "\n" + marker.getTitle() + " " + marker.getPosition();
        }
        text += "\n}";
        return text;
    }
}