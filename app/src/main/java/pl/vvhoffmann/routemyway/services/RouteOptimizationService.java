package pl.vvhoffmann.routemyway.services;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import pl.vvhoffmann.routemyway.models.RouteModel;

import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;

public class RouteOptimizationService {

    public static RouteModel getOptimalRoute() {
        return HeldKarpAlgorithm.getTSPSolution();
    }
}