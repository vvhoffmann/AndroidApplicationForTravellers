package pl.vvhoffmann.routemyway.utils;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;

public class RouteUtilsTest {

    private RoutesHttpClient mockClient;

    @Test
    public void should_return_distance_array_filled_with_mocked_distances_from_points() throws Exception {
        //given
        mockClient = mock(RoutesHttpClient.class);
        RouteUtils.setRoutesHttpClientForTests(mockClient);
        LinkedList<LatLng> latLngList = new LinkedList<>(Arrays.asList(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219),
                new LatLng(52.156614, 21.9822219)
        ));

        LinkedList<Marker> markersList = new LinkedList<>();

        for (LatLng latLng : latLngList) {
            Marker marker = mock(Marker.class);
            when(marker.getPosition()).thenReturn(latLng);
            markersList.add(marker);
        }

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));
        when(mockClient.getWalkingRoute(any(LatLng.class), any(LatLng.class))).thenReturn(5.0);

        // when
        double[][] distanceArray = RouteUtils.getDistanceArray();

        // then
        assertNotNull(distanceArray);
        for(int i=0; i<distanceArray.length; i++) {
            for(int j=0; j<distanceArray[i].length; j++) {
                if(i == j)
                    assertEquals(0.0, distanceArray[i][j]);
                else
                    assertEquals(5.0, distanceArray[i][j]);
            }
        }
    }

}