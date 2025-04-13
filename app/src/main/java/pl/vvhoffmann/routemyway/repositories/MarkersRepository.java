package pl.vvhoffmann.routemyway.repositories;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getLatLngFromMarker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.utils.MarkerUtils;
import pl.vvhoffmann.routemyway.utils.PlacesUtils;

public class MarkersRepository {
    private static Marker currentPositionMarker = null;
    private static LinkedList<Marker> markers = new LinkedList<>();
    private static LinkedHashMap<LatLng,Marker> markersMap = new LinkedHashMap<>();
    private static double[][] distances ;
    private static int size = 1;
    private static boolean areDistancesCalculated = false;

    public static void addMarker(Marker marker) {
        markers.add(marker);
        markersMap.put(getLatLngFromMarker(marker),marker);
        size++;
    }

    public static LinkedList<Marker> getMarkers() {
        return markers;
    }

    public static Marker getMarkerByLatLng(LatLng latLng) {
        return markersMap.get(latLng);
    }

    public static Marker getCurrentPositionMarker() {
        return currentPositionMarker;
    }

    public static void setCurrentPositionMarker(Marker marker) {
        currentPositionMarker = marker;
        markers.addFirst(currentPositionMarker);
        markersMap.put(MarkerUtils.getLatLngFromMarker(currentPositionMarker),currentPositionMarker);
    }

    public static void removeMarker(Marker marker) {
        markers.remove(marker);
        markersMap.remove(getLatLngFromMarker(marker));
        size--;

        if(areDistancesCalculated)
            setDistances();
    }

    public static double[][] getDistanceArray() {
        setDistances();
        return distances;
    }

    private static void setDistances() {
        distances = PlacesUtils.getDistanceArray();
        areDistancesCalculated = true;
    }

    public static LinkedList<LatLng> getLatLngList() {
        LinkedList<LatLng> latLngList = new LinkedList<>();
        for (Marker marker : markers) {
            latLngList.add(marker.getPosition());
        }
        return latLngList;
    }

    public static int getSize() {
        return size;
    }

    public static boolean areDistancesCalculated() {
        return areDistancesCalculated;
    }

    public static boolean containsMarker(Marker selectedMarker) {
        return markersMap.containsKey(selectedMarker.getPosition());
    }

    public static String getDescription() {
        String text =  "MarkersRepository{";
        for (Marker marker : markers) {
            text += "\n" + marker.getTitle() + " " + marker.getPosition();
        }
        text += "\n}";
        return text;
    }
}