package pl.vvhoffmann.routemyway.services;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.utils.MarkerUtils;

public class MapService {
    private static GoogleMap map;

    public static String getGoogleMapsRedirectUrl() {
        LatLng origin = MarkersRepository.getCurrentPositionMarker().getPosition();
        List<LatLng> waypoints = MarkerUtils.getLatLngFromMarkers(RouteRepository.getRoute().getMarkers());
        return buildGoogleMapsRedirectUrl(origin, waypoints);
    }

    @NonNull
    private static String buildGoogleMapsRedirectUrl(LatLng origin, List<LatLng> waypoints) {
        StringBuilder urlBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1");
        urlBuilder.append("&origin=").append(origin.latitude).append(",").append(origin.longitude);
        urlBuilder.append("&destination=").append(origin.latitude).append(",").append(origin.longitude);

        if (!waypoints.isEmpty()) {
            urlBuilder.append("&waypoints=");
            for (int i = 0; i < waypoints.size(); i++) {
                LatLng waypoint = waypoints.get(i);
                if (waypoint.equals(origin))
                    continue;
                urlBuilder.append(waypoint.latitude).append(",").append(waypoint.longitude);
                if (i < waypoints.size() - 1) {
                    urlBuilder.append("|"); // Oddzielanie punktów "|" (pipe symbol)
                }
            }
        }
        return urlBuilder.toString();
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