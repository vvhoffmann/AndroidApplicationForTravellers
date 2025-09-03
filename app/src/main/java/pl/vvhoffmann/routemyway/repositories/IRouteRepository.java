package pl.vvhoffmann.routemyway.repositories;

import pl.vvhoffmann.routemyway.models.RouteModel;

public interface IRouteRepository {

    void saveRoute(RouteModel routeModel);

    RouteModel getRoute();

    boolean isRouteCalculated();

    void resetRoute();
}
