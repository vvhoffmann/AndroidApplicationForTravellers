package pl.vvhoffmann.routemyway.utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface HttpClient {
    double getWalkingRoute(LatLng origin, LatLng destination) throws Exception;
}
