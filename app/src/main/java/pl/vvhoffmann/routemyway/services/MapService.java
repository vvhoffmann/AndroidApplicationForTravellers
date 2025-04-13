package pl.vvhoffmann.routemyway.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;

public class MapService {
    private static GoogleMap map;
    private static LinkedList<MarkerOptions> markerOptionsList;

    public void addMarker(Marker marker) {
        MarkerOptions markerOptions = new MarkerOptions().position(marker.getPosition()).title(marker.getTitle());
        map.addMarker(markerOptions);
        markerOptionsList.add(markerOptions);
    }

    @NonNull
    public static String getRouteUrl() {
        LinkedList<LatLng> routePoints = RouteRepository.getRoute().getMarkers()
                .stream()
                .map(Marker::getPosition)
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

        // Budowanie URL do Directions API
        StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        urlBuilder.append("origin=").append(routePoints.get(0).latitude).append(",").append(routePoints.get(0).longitude);
        urlBuilder.append("&destination=").append(routePoints.get(routePoints.size() - 1).latitude).append(",").append(routePoints.get(routePoints.size() - 1).longitude);
        urlBuilder.append("&waypoints=");
        for (int i = 1; i < routePoints.size() - 1; i++) {
            LatLng waypoint = routePoints.get(i);
            urlBuilder.append(waypoint.latitude).append(",").append(waypoint.longitude);
            if (i < routePoints.size() - 2) {
                urlBuilder.append("|");
            }
        }
        urlBuilder.append("&mode=walking");
        urlBuilder.append("&key=").append(AppConfig.GOOGLE_MAPS_API_KEY);

        return urlBuilder.toString();
    }

    public static GoogleMap getMap() {
        return map;
    }

    public static void setMap(GoogleMap googleMap) {
        map = googleMap;
    }
}