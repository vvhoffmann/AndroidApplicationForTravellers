package pl.vvhoffmann.routemyway.utils;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.LinkedList;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;

public class HeldKarpAlgorithmTest {

    @Test
    public void should_return_tsp_solution_when_list_of_points_given()
    {
        //given
        LinkedList<LatLng> latLngList = new LinkedList<>();
        latLngList.add(new LatLng(4.0, 23.0));
        latLngList.add(new LatLng(50.0, 25.0));
        latLngList.add(new LatLng(70.0, 20.0));
        latLngList.add(new LatLng(35.0, 90.0));

        LinkedList<Marker> markersList = new LinkedList<>();

        for (LatLng latLng : latLngList) {
            Marker marker = mock(Marker.class);
            when(marker.getPosition()).thenReturn(latLng);
            markersList.add(marker);
        }

        MarkersRepositoryTestImpl testRepo = new MarkersRepositoryTestImpl(markersList);
        MarkersRepository.setInstanceForTests(testRepo);

        // when
        LinkedList<LatLng> tspSolutionPointList = HeldKarpAlgorithm.getTSPSolution();
        double distance = HeldKarpAlgorithm.getDistance();

        //then
        assertTrue(tspSolutionPointList.containsAll(latLngList));
        assertEquals(distance, 218.0, 1.0);

        LinkedList<LatLng> latLngResultList = new LinkedList<>();
        latLngResultList.add(new LatLng(4.0, 23.0));
        latLngResultList.add(new LatLng(35.0, 90.0));
        latLngResultList.add(new LatLng(70.0, 20.0));
        latLngResultList.add(new LatLng(50.0, 25.0));
        latLngResultList.add(new LatLng(4.0, 23.0));

        assertEquals(tspSolutionPointList, latLngResultList);

    }
}