package pl.vvhoffmann.routemyway.utils;

import static android.content.ContentValues.TAG;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

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

    public static double[][] getDistanceArray() {
        LinkedList<LatLng> points = MarkersRepository.getLatLngList();

        int n = points.size();
        double[][] dist = new double[n][n];

        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<Double>> results = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i, col = j;
                if(row == col)
                    results.add(executor.submit(() -> 0.0));
                else
                    results.add(executor.submit(() -> getWalkingRoute(points.get(row), points.get(col))));
            }
        }

        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                try {
                    dist[i][j] = results.get(index++).get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        executor.shutdown();
        return dist;
    }


    public static double getWalkingRoute(LatLng origin, LatLng destination) throws Exception {
        String response = getPlacesAPIResponse(origin, destination);
        if (response == null)
            throw new IOException("Brak odpowiedzi z API");
        return parseDistance(response);
    }

    @NonNull
    public static String getPlacesAPIResponse(LatLng origin, LatLng destination) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(origin, destination);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            response.append(line);

        bufferedReader.close();
        return response.toString();
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(LatLng origin, LatLng destination) throws IOException {
        String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking"
                + "&key=" + AppConfig.GOOGLE_MAPS_API_KEY;

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
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
}
