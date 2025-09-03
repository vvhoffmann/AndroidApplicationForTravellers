package pl.vvhoffmann.routemyway.repositories;

import pl.vvhoffmann.routemyway.models.RouteModel;

public class RoutesRepositoryTestImpl implements IRouteRepository {

    private RouteModel route;
    private boolean isRouteCalculated = false;

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
