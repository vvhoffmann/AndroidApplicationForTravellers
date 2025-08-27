package pl.vvhoffmann.routemyway.utils;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;

public class WalkingRouteHttpClientTest {

    @Test
    public void should_getWalkingRoute_return_reasonable_distance() throws Exception {
        // given
        RoutesHttpClient client = mock(WalkingRouteHttpClient.class);
        LatLng origin = new LatLng(52.2296756, 21.0122287); // Warsaw
        LatLng destination = new LatLng(41.8919300, 12.5113300); // Rome

        String fakeResponse = "{ \"routes\": [ { \"legs\": [ { \"distance\": { \"text\": \"1,432 km\", \"value\": 1432000 } } ] } ] }";
        when(client.getWalkingRoute(origin, destination)).thenReturn(1432000.0);

        // when
        double distance = client.getWalkingRoute(origin, destination);

        // then
        assertTrue(distance>0.0);
    }
}