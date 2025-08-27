package pl.vvhoffmann.routemyway.utils;

import com.google.android.gms.maps.model.LatLng;

public interface RoutesHttpClient {
    double getWalkingRoute(LatLng origin, LatLng destination) throws Exception;
}
