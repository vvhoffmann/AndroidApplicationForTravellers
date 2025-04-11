package pl.vvhoffmann.routemyway.activities.mapsActivity;

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
