package pl.vvhoffmann.routemyway.repositories;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;

public interface IMarkersRepository {

    public void addMarker(Marker marker);

    public LinkedList<Marker> getMarkers();

    public Marker getMarkerByLatLng(LatLng latLng);

    public Marker getCurrentPositionMarker();
    public void setCurrentPositionMarker(Marker marker);

    public void removeMarker(Marker marker);

    public double[][] getDistanceArray();

    public LinkedList<LatLng> getLatLngList();

    public int getSize();

    public boolean areDistancesCalculated();

    public boolean containsMarker(Marker selectedMarker);

    public String getDescription();
}
