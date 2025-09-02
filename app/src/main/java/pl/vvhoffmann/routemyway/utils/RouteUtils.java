package pl.vvhoffmann.routemyway.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

public class RouteUtils {

    public static double[][] getDistanceArray() {
        List<LatLng> points = MarkersRepository.getInstance().getLatLngList();

        int n = points.size();
        double[][] dist = new double[n][n];

        RoutesHttpClient routesHttpClient = new WalkingRouteHttpClient();
        List<Future<Double>> results = getRoutesLengths(n, routesHttpClient, points);

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
        return dist;
    }

    private static List<Future<Double>> getRoutesLengths(int n, RoutesHttpClient routesHttpClient, List<LatLng> points) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<Double>> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i, col = j;
                if(row == col)
                    results.add(executor.submit(() -> 0.0));
                else
                    results.add(executor.submit(() -> routesHttpClient.getWalkingRoute(points.get(row), points.get(col))));
            }
        }
        executor.shutdown();
        return results;
    }

    public static double getRouteLength(List<LatLng> routePoints) {
        RoutesHttpClient routesHttpClient = new WalkingRouteHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        double distance =0.0;
        for (int i=0 ; i<routePoints.size()-1 ; i++) {
            LatLng starting = routePoints.get(i);
            LatLng destination = routePoints.get(i + 1);
            try {
                distance += executor.submit(() -> routesHttpClient.getWalkingRoute(starting, destination)).get();
            } catch (Exception e) {
                e.getMessage();
            }
        }
        executor.shutdown();
        return distance;
    }
}
