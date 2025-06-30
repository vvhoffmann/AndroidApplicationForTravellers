package pl.vvhoffmann.routemyway.services;

import pl.vvhoffmann.routemyway.models.RouteModel;

import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;

public class RouteOptimizationService {
    public static RouteModel getOptimalRoute() {
        RouteModel routeModel = new RouteModel(HeldKarpAlgorithm.getTSPSolution());
        RouteRepository.saveRoute(routeModel);
        return routeModel;
    }
}