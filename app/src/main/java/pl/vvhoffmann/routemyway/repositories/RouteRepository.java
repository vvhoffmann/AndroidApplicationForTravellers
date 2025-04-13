package pl.vvhoffmann.routemyway.repositories;

import pl.vvhoffmann.routemyway.models.RouteModel;

public class RouteRepository {
    private static RouteModel route;
    private static boolean isRouteCalculated = false;

    public static void saveRoute(RouteModel newRoute) {
        route = newRoute;
        isRouteCalculated = true;
    }

    public static RouteModel getRoute() {
        if (!isRouteCalculated) {
            throw new IllegalStateException("Route not calculated yet");
        }
        return route;
    }
    public static boolean isRouteCalculated() {
        return isRouteCalculated;
    }

}