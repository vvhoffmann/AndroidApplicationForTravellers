package com.example.bachelorthesisapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

public class DirectionsHelper {

    private GeoApiContext context;

    public DirectionsHelper() {
        context = new GeoApiContext.Builder()
                .apiKey("${PLACES_API_KEY}")
                .build();
    }

    public DirectionsResult getWalkingRoute(LatLng origin, LatLng destination) throws Exception {
        DirectionsResult result = DirectionsApi.newRequest(context)
                .mode(com.google.maps.model.TravelMode.WALKING) // Ustawienie transportu pieszo
                .origin(String.valueOf(origin))
                .destination(String.valueOf(destination))
                .await(); // Wykonanie zapytania i oczekiwanie na wynik
        return result;
    }

    public void displayRoute(DirectionsResult result, RouteFragment context) {
        for (DirectionsRoute route : result.routes) {
            for (DirectionsLeg leg : route.legs) {
                // Drukowanie informacji o trasie
                //Toast.makeText("Trasa: " + leg.startAddress + " -> " + leg.endAddress);
                //Toast.makeText("Czas: " + leg.duration.humanReadable);
                System.out.println("Odległość: " + leg.distance.humanReadable);
                //Toast.makeText("Szczegóły trasy: " + leg.steps);
            }
        }
    }
}
