package pl.vvhoffmann.routemyway.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

public class MarkerUtils {

    // dawniej createStringFromMarker(Marker marker)
    public static String createMarkerDescription(Marker marker) {
        return  marker.getTitle() + "\n[ "
                + marker.getPosition().latitude + " , "
                + marker.getPosition().longitude + " ]";
    }

    public static Marker createMarkerFromString(String text) {

        if(!text.contains("]"))
            for(Marker marker : MarkersRepository.getInstance().getMarkers())
                if(text.contains(marker.getTitle()))
                    return marker;

        text = text.replaceAll("\\]", "");
        String[] parts = text.split("\\[");
        String[] coordinates = parts[1].trim().split(",");

        double lat = Double.parseDouble(coordinates[0].trim());
        double lng = Double.parseDouble(coordinates[1].trim());
        LatLng position = new LatLng(lat, lng);

        for (LatLng latLng : MarkersRepository.getInstance().getLatLngList())
            if (latLng.equals(position))
                return getMarkerByLatLng(position);

        return null;
    }

    public static Marker getMarkerByLatLng(LatLng latLng) {
        return MarkersRepository.getInstance().getMarkerByLatLng(latLng);
    }

    public static LatLng getLatLngFromMarker(Marker marker) {
        return marker.getPosition();
    }

    public static List<LatLng> getLatLngFromMarkers(LinkedList<Marker> markers) {
        List<LatLng> latLngList = new LinkedList<>();
        for (Marker marker : markers)
            latLngList.add(getLatLngFromMarker(marker));

        return latLngList;

    }
}
