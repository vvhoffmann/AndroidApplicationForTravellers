package pl.vvhoffmann.routemyway.utils;

import static android.content.ContentValues.TAG;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
            else{
                placeDescription = "[ " + latLng.latitude + ", " + latLng.longitude + " ]";
            }

            /*
            if(address.getFeatureName() != null && address.getFeatureName().matches(".*[" + characters + "].*") )
                placeDescription = address.getFeatureName();
            else
                placeDescription =  "ul. " + address.getThoroughfare() + " " + address.getSubThoroughfare() + ", " + address.getLocality();
            */
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving place description", e);
        }

        return placeDescription;
    }

    public static double[][] getDistanceArray() {
        LinkedList<LatLng> points = MarkersRepository.getLatLngList();

        int n = MarkersRepository.getSize();
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
                try {
                    dist[i][j] = results.get(index++).get(); // Pobieranie wyniku
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
                + "&key=" + AppConfig.GOOGLE_MAPS_API_KEY;

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
}
