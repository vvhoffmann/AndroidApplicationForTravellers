package pl.vvhoffmann.routemyway.utils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;
import pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm.QuasiOptimizationAlgorithm;

public class QuasiOptimizationAlgorithmTest {

    @Test
    public void should_return_quasi_optimized_path_from_given_points_which_starts_with_current_position_marker() throws Exception {
        //given
        RoutesHttpClient mockClient = mock(RoutesHttpClient.class);
        RouteUtils.setRoutesHttpClientForTests(mockClient);
        LinkedList<LatLng> latLngList = new LinkedList<>(Arrays.asList(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219),
                new LatLng(52.156614, 21.9822219),
                new LatLng(52.756614, 21.8822219),
                new LatLng(52.456614, 21.5622219),
                new LatLng(52.216614, 21.6422219),
                new LatLng(52.216614, 21.5432219),
                new LatLng(52.286614, 21.9322219)
        ));

        LinkedList<Marker> markersList = new LinkedList<>();

        for (LatLng latLng : latLngList) {
            Marker marker = mock(Marker.class);
            when(marker.getPosition()).thenReturn(latLng);
            markersList.add(marker);
        }

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));
        when(mockClient.getWalkingRoute(latLngList.get(0), latLngList.get(1))).thenReturn(50.0);
        when(mockClient.getWalkingRoute(latLngList.get(1), latLngList.get(2))).thenReturn(70.0);
        when(mockClient.getWalkingRoute(latLngList.get(2), latLngList.get(3))).thenReturn(90.0);
        when(mockClient.getWalkingRoute(latLngList.get(3), latLngList.get(4))).thenReturn(130.0);
        when(mockClient.getWalkingRoute(latLngList.get(4), latLngList.get(5))).thenReturn(60.0);
        when(mockClient.getWalkingRoute(latLngList.get(5), latLngList.get(6))).thenReturn(20.0);
        when(mockClient.getWalkingRoute(latLngList.get(6), latLngList.get(7))).thenReturn(45.0);
        when(mockClient.getWalkingRoute(latLngList.get(7), latLngList.get(8))).thenReturn(54.0);
        when(mockClient.getWalkingRoute(latLngList.get(8), latLngList.get(0))).thenReturn(78.0);

        // when
        List<LatLng> tspSolutionList = QuasiOptimizationAlgorithm.getTSPSolution();

        // then
        assertNotNull(tspSolutionList);
        assertEquals(latLngList.size() + 1, tspSolutionList.size());
        assertEquals(MarkersRepository.getInstance().getCurrentPositionMarker().getPosition(), tspSolutionList.get(0));
        assertEquals(MarkersRepository.getInstance().getCurrentPositionMarker().getPosition(), tspSolutionList.get(tspSolutionList.size() - 1));
        assertTrue(tspSolutionList.containsAll(latLngList));
        assertTrue(QuasiOptimizationAlgorithm.getDistance() > 0);

        List<LatLng> roundBaseRoute = new LinkedList<>(latLngList);
        roundBaseRoute.add(latLngList.get(0));
        assertTrue(QuasiOptimizationAlgorithm.getDistance() <= RouteUtils.getRouteLength(roundBaseRoute));
    }

}