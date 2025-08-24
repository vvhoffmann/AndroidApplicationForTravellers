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
        LinkedList<LatLng> points = MarkersRepository.getInstance().getLatLngList();

        int n = points.size();
        double[][] dist = new double[n][n];

        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<Double>> results = new ArrayList<>();
        HttpClient httpClient = new WalkingRouteHttpClient();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i, col = j;
                if(row == col)
                    results.add(executor.submit(() -> 0.0));
                else
                    results.add(executor.submit(() -> httpClient.getWalkingRoute(points.get(row), points.get(col))));
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
}
