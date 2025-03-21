package com.example.bachelorthesisapp.mapsActivity;

import com.example.bachelorthesisapp.PointUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HeldKarpAlgorithm {

    private static final double INF = Integer.MAX_VALUE;

    public static ArrayList<LatLng> getTSPSolution(ArrayList<LatLng> points, double [][] dist) {
        int n = points.size();
        int N = 1 << n; // 2^n
        double[][] dp = new double[N][n];
        int[][] parent = new int[N][n]; // Do rekonstrukcji ścieżki

        for (double[] row : dp) Arrays.fill(row, INF);
        dp[1][0] = 0; // Start w punkcie 0

        //double[][] dist = PointUtils.getDistanceArray(points);

        // Wypełnianie tablicy DP
        for (int subset = 1; subset < N; subset++) {
            if ((subset & 1) == 0) continue; // Musi zawierać punkt startowy

            for (int j = 1; j < n; j++) { // Ostatnie miasto
                if ((subset & (1 << j)) == 0) continue; // j nie w zbiorze

                int prevSubset = subset ^ (1 << j); // Usunięcie j z podzbioru
                double minCost = INF;
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
        double minTourCost = INF;
        int lastCity = -1;

        for (int j = 1; j < n; j++) {
            double cost = dp[fullSet][j] + dist[j][0];
            if (cost < minTourCost) {
                minTourCost = cost;
                lastCity = j;
            }
        }

        // Rekonstrukcja ścieżki
        ArrayList<LatLng> path = reconstructPath(parent, fullSet, lastCity, points);
        path.add(points.get(0)); // Powrót do startu
        return path;
    }

    private static ArrayList<LatLng> reconstructPath(int[][] parent, int subset, int last, ArrayList<LatLng> points) {
        ArrayList<LatLng> path = new ArrayList<>();
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

