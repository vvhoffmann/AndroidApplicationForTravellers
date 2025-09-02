package pl.vvhoffmann.routemyway.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.vvhoffmann.routemyway.config.AppConfig;

public class WalkingRouteHttpClient implements RoutesHttpClient {

    @Override
    public double getWalkingRoute(LatLng origin, LatLng destination) throws Exception {
        String response = getRoutesAPIResponse(origin, destination);
        if (response == null)
        {
            try {
                throw new IOException("Brak odpowiedzi z API");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return parseDistance(response);
    }

    @NonNull
    private String getRoutesAPIResponse(LatLng origin, LatLng destination) {
        HttpURLConnection connection = getHttpUrlConnection(origin, destination);
        StringBuilder response = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String line;
            while ((line = bufferedReader.readLine()) != null)
                response.append(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.toString();
    }

    private static double parseDistance(String jsonResponse) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");

        if (routes.length() > 0) {
            double distanceValue = parseJSONObjectToRouteLength(routes);
            return Converter.convertMetersToKm(distanceValue);
        } else {
            throw new Exception("Brak trasy w odpowiedzi API");
        }
    }

    private static double parseJSONObjectToRouteLength(JSONArray routes) throws JSONException {
        JSONObject route = routes.getJSONObject(0);
        JSONArray legs = route.getJSONArray("legs");

        JSONObject leg = legs.getJSONObject(0);
        JSONObject distance = leg.getJSONObject("distance");

        return distance.getDouble("value");
    }

    private HttpURLConnection getHttpUrlConnection(LatLng origin, LatLng destination) {
        String requestUrl = prepareRequestUrl(origin, destination);
        HttpURLConnection connection;

        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    @NonNull
    private static String prepareRequestUrl(LatLng origin, LatLng destination) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking"
                + "&key=" + AppConfig.GOOGLE_MAPS_API_KEY;
    }
}