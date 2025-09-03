package pl.vvhoffmann.routemyway.utils;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import com.google.android.gms.maps.model.LatLng;

public class WalkingRouteHttpClientTest {

    @Test
    public void should_getWalkingRoute_return_reasonable_distance() throws Exception {
        // given
        String mockApiResponse = "{ \"routes\": [ { \"legs\": [ { \"distance\": { \"text\": \"1,432 km\", \"value\": 1432000 } } ] } ] }";

        // Tworzymy spy na obiekcie klasy, żeby nadpisać tylko metodę getRoutesAPIResponse
        WalkingRouteHttpClient client = Mockito.spy(new WalkingRouteHttpClient());
        LatLng origin = new LatLng(52.2296756, 21.0122287);
        LatLng destination = new LatLng(41.8919300, 12.5113300);

        // Zamockuj metodę getRoutesAPIResponse
        Mockito.doReturn(mockApiResponse)
                .when(client)
                .getRoutesAPIResponse(origin, destination);

        // when
        double distanceKm = client.getWalkingRoute(origin, destination);

        // then
        assertEquals(1432.0, distanceKm, 0.01);
    }
}