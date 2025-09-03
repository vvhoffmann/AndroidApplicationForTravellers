package pl.vvhoffmann.routemyway.services;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.models.RouteModel;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.repositories.RoutesRepositoryTestImpl;

public class MapServiceTest {

    @BeforeClass
    public static void setUp() {
        //given
        List<LatLng> latLngList = List.of(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219),
                new LatLng(52.156614, 21.9822219)
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
        RouteRepository.setInstanceForTests(new RoutesRepositoryTestImpl(new RouteModel(latLngList)));
    }

    @Test
    public void should_return_marker_from_string() {
        // when
        String googleMapsRedirectUrl = MapService.getGoogleMapsRedirectUrl();

        // then
        String expectedUrl = "https://www.google.com/maps/dir/?api=1" +
                "&origin=52.2296756,21.0122287" +
                "&destination=52.2296756,21.0122287" +
                "&waypoints=52.89193,21.51133|52.856614,21.3522219|52.156614,21.9822219";
        assertEquals(expectedUrl, googleMapsRedirectUrl);
    }

    @Test
    public void should_return_routeUrl() {
        // when
        String googleMapsRedirectUrl = MapService.getRouteUrl();

        // then
        String expectedUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=52.2296756,21.0122287" +
                "&destination=52.156614,21.9822219" +
                "&waypoints=52.89193,21.51133|52.856614,21.3522219" +
                "&mode=walking" +
                "&key=" + AppConfig.GOOGLE_MAPS_API_KEY;
        assertEquals(expectedUrl, googleMapsRedirectUrl);
    }

    @Test
    public void should_set_map_in_mapservice() {
        // when
        MapService.setMap(mock(GoogleMap.class));

        // then
        assertNotNull(MapService.getMap());
    }
}