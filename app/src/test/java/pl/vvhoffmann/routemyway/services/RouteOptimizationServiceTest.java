package pl.vvhoffmann.routemyway.services;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;
import pl.vvhoffmann.routemyway.utils.TSPAlgorithm;

public class RouteOptimizationServiceTest {

    @Test
    public void should_return_quasi_optimal_solution_when_more_than_15_points_given() {
        //given
        List<LatLng> latLngList = List.of(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219),
                new LatLng(52.156614, 21.9822219),
                new LatLng(52.756614, 21.8822219),
                new LatLng(52.456614, 21.5622219),
                new LatLng(52.216614, 21.6422219),
                new LatLng(52.216614, 21.5432219),
                new LatLng(52.286614, 21.9322219),
                new LatLng(52.326614, 21.8322219),
                new LatLng(52.366614, 21.7322219),
                new LatLng(52.406614, 21.6322219),
                new LatLng(52.446614, 21.5322219),
                new LatLng(52.486614, 21.4322219),
                new LatLng(52.526614, 21.3322219),
                new LatLng(52.566614, 21.2322219)
        );

        LinkedList<Marker> markersList = IntStream.range(0, latLngList.size())
                .mapToObj(i -> {
                    LatLng latLng = latLngList.get(i);
                    Marker marker = mock(Marker.class);
                    when(marker.getPosition()).thenReturn(latLng);

                    String title = (i == 0) ? "ul. Tamka 35, Warszawa" : "Marker " + i;
                    when(marker.getTitle()).thenReturn(title);

                    return marker;
                })
                .collect(Collectors.toCollection(LinkedList::new));

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));

        // when
        RouteModel routeModel = RouteOptimizationService.getOptimalRoute();

        // then
        assertEquals(MarkersRepository.getInstance().getLatLngList().size() + 1, routeModel.getSize());
        assertEquals(TSPAlgorithm.QUASI_OPTIMAL, RouteOptimizationService.usedAlgorithm);
    }

    @Test
    public void should_return_heldkarp_solution_when_less_than_15_points_given() {
        //given
        List<LatLng> latLngList = List.of(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219),
                new LatLng(52.156614, 21.9822219),
                new LatLng(52.756614, 21.8822219),
                new LatLng(52.456614, 21.5622219)
        );

        LinkedList<Marker> markersList = IntStream.range(0, latLngList.size())
                .mapToObj(i -> {
                    LatLng latLng = latLngList.get(i);
                    Marker marker = mock(Marker.class);
                    when(marker.getPosition()).thenReturn(latLng);

                    String title = (i == 0) ? "ul. Tamka 35, Warszawa" : "Marker " + i;
                    when(marker.getTitle()).thenReturn(title);

                    return marker;
                })
                .collect(Collectors.toCollection(LinkedList::new));

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));

        // when
        RouteModel routeModel = RouteOptimizationService.getOptimalRoute();

        // then
        assertEquals(MarkersRepository.getInstance().getLatLngList().size() + 1, routeModel.getSize());
        assertEquals(TSPAlgorithm.HELD_KARP, RouteOptimizationService.usedAlgorithm);
    }
}