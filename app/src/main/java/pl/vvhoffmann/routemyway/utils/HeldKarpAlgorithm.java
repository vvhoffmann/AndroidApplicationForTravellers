package pl.vvhoffmann.routemyway.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

public class HeldKarpAlgorithm {
    public static RouteModel getTSPSolution() {

        LatLng startPoint = MarkersRepository.getCurrentPositionMarker().getPosition();
        int n = MarkersRepository.getMarkers().size();
        double[][] dist = MarkersRepository.getDistanceArray();
        LinkedList<LatLng> points = MarkersRepository.getLatLngList();

        int N = 1 << n; // 2^n
        double[][] dp = new double[N][n];
        int[][] parent = new int[N][n]; // Do rekonstrukcji ścieżki

        for (double[] row : dp) Arrays.fill(row, Constants.MAX_VALUE);
        dp[1][0] = 0; // Start w punkcie 0

        //double[][] dist = PointUtils.getDistanceArray(points);

        // Wypełnianie tablicy DP
        for (int subset = 1; subset < N; subset++) {
            if ((subset & 1) == 0) continue; // Musi zawierać punkt startowy

            for (int j = 1; j < n; j++) { // Ostatnie miasto
                if ((subset & (1 << j)) == 0) continue; // j nie w zbiorze

                int prevSubset = subset ^ (1 << j); // Usunięcie j z podzbioru
                double minCost = Constants.MAX_VALUE;
                int bestK = -1;

                for (int k = 0; k < n; k++) {
                    if ((prevSubset & (1 << k)) != 0) { // k jest w zbiorze
                        double newCost = dp[prevSubset][k] + dist[k][j];
                        if (newCost < minCost) {
                            minCost = newCost;
                            bestK = k;
                        }
                    }
                }
                dp[subset][j] = minCost;
                parent[subset][j] = bestK; // Zapamiętujemy ścieżkę
            }
        }

        // Odczytanie minimalnej wartości
        int fullSet = N - 1;
        double minTourCost = Constants.MAX_VALUE;;
        int lastCity = -1;

        for (int j = 1; j < n; j++) {
            double cost = dp[fullSet][j] + dist[j][0];
            if (cost < minTourCost) {
                minTourCost = cost;
                lastCity = j;
            }
        }

        // Rekonstrukcja ścieżki
        LinkedList<LatLng> path = reconstructPath(parent, fullSet, lastCity, points);
        path.addLast(startPoint); // Powrót do startu
        return new RouteModel(path);
    }

    private static LinkedList<LatLng> reconstructPath(int[][] parent, int subset, int last, LinkedList<LatLng> points) {
        LinkedList<LatLng> path = new LinkedList<>();
        while (last != 0) {
            path.add(points.get(last));
            int prevSubset = subset ^ (1 << last);
            last = parent[subset][last];
            subset = prevSubset;
        }
        path.add(points.get(0)); // Dodaj punkt startowy
        Collections.reverse(path);
        return path;
    }
}

