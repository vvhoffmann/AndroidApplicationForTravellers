package pl.vvhoffmann.routemyway.repositories;

import pl.vvhoffmann.routemyway.models.RouteModel;

public class RouteRepository {
    private RouteModel route;

    public void saveRoute(RouteModel route) {
        this.route = route;
    }

    public RouteModel getRoute() {
        return route;
    }


}