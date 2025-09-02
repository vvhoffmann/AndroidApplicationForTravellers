package pl.vvhoffmann.routemyway.repositories;

import static pl.vvhoffmann.routemyway.utils.MarkerUtils.getLatLngFromMarker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.utils.MarkerUtils;
import pl.vvhoffmann.routemyway.utils.PointUtils;

public class MarkersRepositoryTestImpl implements IMarkersRepository{

    private static MarkersRepositoryTestImpl MARKERS_REPOSITORY;
    private Marker currentPositionMarker = null;
    private LinkedList<Marker> markers = new LinkedList<>();
    private LinkedHashMap<LatLng,Marker> markersMap = new LinkedHashMap<>();
    private double[][] distances ;
    private int size = 1;
    private boolean areDistancesCalculated = false;

    public MarkersRepositoryTestImpl(LinkedList<Marker> markersToAdd) {
        setCurrentPositionMarker(markersToAdd.get(0));
        for (Marker marker : markersToAdd) {
            if(marker == currentPositionMarker) continue;
            markers.add(marker);
            markersMap.put(getLatLngFromMarker(marker),marker);
            size++;
        }
        MARKERS_REPOSITORY = this;

    }

    public static MarkersRepositoryTestImpl getInstance() {
        return MARKERS_REPOSITORY;
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
        distances = generateDistanceArray();
        areDistancesCalculated = true;
    }

    private double[][] generateDistanceArray() {
        double[][] distanceArray = new double[size][size];
        for (Marker markerA : markers) {
            for (Marker markerB : markers) {
                int indexA = markers.indexOf(markerA);
                int indexB = markers.indexOf(markerB);
                if (markerA == markerB)
                    distanceArray[indexA][indexB] = 0.0;
                else
                    distanceArray[indexA][indexB] = getDistanceBetweenMarkers(markerA, markerB);
            }
        }
        return distanceArray;
    }

    private double getDistanceBetweenMarkers(Marker markerA, Marker markerB) {
        return PointUtils.distance(markerA.getPosition(), markerB.getPosition());
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