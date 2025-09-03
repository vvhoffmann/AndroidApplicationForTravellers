package pl.vvhoffmann.routemyway.repositories;

import pl.vvhoffmann.routemyway.models.RouteModel;

public class RouteRepository implements IRouteRepository {

    private static IRouteRepository routeRepository = new RouteRepository();
    private RouteModel route;
    private boolean isRouteCalculated = false;

    public static IRouteRepository getInstance() {
        return routeRepository;
    }

    public static void setInstanceForTests(IRouteRepository instance) {
        routeRepository = instance;
    }

    private RouteRepository() {}

    @Override
    public void saveRoute(RouteModel newRoute) {
        route = newRoute;
        isRouteCalculated = true;
    }

    @Override
    public RouteModel getRoute() {
        if (!isRouteCalculated) {
            throw new IllegalStateException("Route not calculated yet");
        }
        return route;
    }

    @Override
    public boolean isRouteCalculated() {
        return isRouteCalculated;
    }

    @Override
    public void resetRoute() {
        route = null;
        isRouteCalculated = false;
    }
}