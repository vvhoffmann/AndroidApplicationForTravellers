package com.example.bachelorthesisapp.mapsActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DirectionsHelper {

    private GeoApiContext context;
    private static final String API_KEY = "AIzaSyAysSR_bO84Y4HF7NLNwkFjpGIN1CnfMSM";

    public static double[][] getDistanceArray(ArrayList<LatLng> points) throws Exception {
        int n = points.size();
        double[][] dist = new double[n][n]; // Macierz odległości

        // Używamy ExecutorService do obsługi wielu zapytań jednocześnie
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<Double>> results = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i, col = j;
                results.add(executor.submit(() -> getWalkingRoute(points.get(row), points.get(col))));
            }
        }

        // Odbieramy wyniki i zapisujemy w macierzy
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = results.get(index++).get(); // Pobieranie wyniku
            }
        }

        executor.shutdown(); // Zatrzymujemy wątki
        return dist;
    }



    public static double getWalkingRoute(LatLng origin, LatLng destination) throws Exception {
        String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking"
                + "&key=" + API_KEY;

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }

        return parseDistance(response.toString());
    }

    private static double parseDistance(String jsonResponse) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");

        if (routes.length() > 0) {
            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");

            JSONObject leg = legs.getJSONObject(0);
            JSONObject distance = leg.getJSONObject("distance");

            double distanceValue = distance.getDouble("value"); // W metrach
            return distanceValue / 1000.0; // Konwersja na kilometry
        } else {
            throw new Exception("Brak trasy w odpowiedzi API");
        }
    }



//    public void displayRoute(DirectionsResult result, RouteFragment context) {
//        for (DirectionsRoute route : result.routes) {
//            for (DirectionsLeg leg : route.legs) {
//                // Drukowanie informacji o trasie
//                //Toast.makeText("Trasa: " + leg.startAddress + " -> " + leg.endAddress);
//                //Toast.makeText("Czas: " + leg.duration.humanReadable);
//                System.out.println("Odległość: " + leg.distance.humanReadable);
//                //Toast.makeText("Szczegóły trasy: " + leg.steps);
//            }
//        }
//    }
}
