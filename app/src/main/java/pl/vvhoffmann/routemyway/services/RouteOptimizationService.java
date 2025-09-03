package pl.vvhoffmann.routemyway.services;

import static pl.vvhoffmann.routemyway.constants.Constants.MIN_POINTS_TO_RUN_OPTIMAL_ALGORITHM;

import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.utils.HeldKarpAlgorithm;
import pl.vvhoffmann.routemyway.utils.TSPAlgorithm;
import pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm.QuasiOptimizationAlgorithm;

public class RouteOptimizationService {

    public static TSPAlgorithm usedAlgorithm;
    public static RouteModel getOptimalRoute() {
        RouteModel routeModel;
        if(MarkersRepository.getInstance().getLatLngList().size() < MIN_POINTS_TO_RUN_OPTIMAL_ALGORITHM){
            usedAlgorithm = TSPAlgorithm.HELD_KARP;
            routeModel = new RouteModel(HeldKarpAlgorithm.getTSPSolution());
        }
        else {
            usedAlgorithm = TSPAlgorithm.QUASI_OPTIMAL;
            routeModel = new RouteModel(QuasiOptimizationAlgorithm.getTSPSolution());
        }
        RouteRepository.getInstance().saveRoute(routeModel);
        return routeModel;
    }
}