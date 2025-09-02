package pl.vvhoffmann.routemyway.utils;

import static android.content.ContentValues.TAG;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PlacesUtils {

    public static String getPlaceDescription(LatLng latLng, Geocoder geocoder) {
        String placeDescription = "Nie znaleziono opisu miejsca";

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String characters = "abcdefghijklmnopqrstuvwxyz";
                String text = "ul. " + address.getThoroughfare() + " " + address.getSubThoroughfare() + ", " + address.getLocality();
                placeDescription = !address.getFeatureName().matches(".*[" + characters + "].*") ? text : address.getFeatureName() + ", " + text;
            }
            else
                placeDescription = "[ " + latLng.latitude + ", " + latLng.longitude + " ]";

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving place description", e);
        }

        return placeDescription;
    }
}