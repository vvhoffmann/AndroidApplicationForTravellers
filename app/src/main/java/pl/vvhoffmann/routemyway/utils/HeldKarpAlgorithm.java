package pl.vvhoffmann.routemyway.utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

public class HeldKarpAlgorithm {
    private static final double INF = Constants.MAX_VALUE;
    private static double distance = 0.0;

    public static LinkedList<LatLng> getTSPSolution() {
        LinkedList<LatLng> points = MarkersRepository.getInstance().getLatLngList();
        double [][] dist = MarkersRepository.getInstance().getDistanceArray();
        return calculateHeldKarpSolution(points,dist);
    }

    private static LinkedList<LatLng> calculateHeldKarpSolution(LinkedList<LatLng> points, double[][] dist) {
        int n = points.size();

        int N = 1 << n; // 2^n
        double[][] dp = new double[N][n];
        int[][] parent = new int[N][n];

        for (double[] row : dp) Arrays.fill(row, INF);
        dp[1][0] = 0;

        // Wypełnianie tablicy DP
        for (int subset = 1; subset < N; subset++) {
            if ((subset & 1) == 0) continue;

            for (int j = 1; j < n; j++) {
                if ((subset & (1 << j)) == 0) continue;

                int prevSubset = subset ^ (1 << j);
                double minCost = INF;
                int bestK = -1;

                for (int k = 0; k < n; k++) {
                    if ((prevSubset & (1 << k)) != 0) {
                        double newCost = dp[prevSubset][k] + dist[k][j];
                        if (newCost < minCost) {
                            minCost = newCost;
                            bestK = k;
                        }
                    }
                }
                dp[subset][j] = minCost;
                parent[subset][j] = bestK;
            }
        }

        // Odczytanie minimalnej wartości
        int fullSet = N - 1;
        double minTourCost = INF;
        int lastCity = -1;

        for (int j = 1; j < n; j++) {
            double cost = dp[fullSet][j] + dist[j][0];
            if (cost < minTourCost) {
                minTourCost = cost;
                lastCity = j;
            }
        }

        distance = minTourCost;
        // Rekonstrukcja ścieżki
        LinkedList<LatLng> path = reconstructPath(parent, fullSet, lastCity, points);
        path.add(points.get(0));
        return path;
    }

    private static LinkedList<LatLng> reconstructPath(int[][] parent, int subset, int last, LinkedList<LatLng> points) {
        LinkedList<LatLng> path = new LinkedList<>();
        while (last != 0) {
            path.add(points.get(last));
            int prevSubset = subset ^ (1 << last);
            last = parent[subset][last];
            subset = prevSubset;
        }
        path.add(points.get(0));
        Collections.reverse(path);
        return path;
    }

    public static double getDistance() {
        return distance;
    }
}