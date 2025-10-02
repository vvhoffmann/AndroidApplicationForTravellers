package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.fragment.app.testing.FragmentScenario;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import pl.vvhoffmann.routemyway.services.MapService;

@RunWith(AndroidJUnit4.class)
public class MapFragmentInstrumentedTest {


    @Test
    public void should_drawRoute_does_not_throw_exception() {
        GoogleMap mockMap = mock(GoogleMap.class);
        MapService.setMap(mockMap);

        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        List<LatLng> points = Arrays.asList(
                new LatLng(52.1, 21.0),
                new LatLng(52.2, 21.1),
                new LatLng(52.3, 21.2)
        );

        scenario.onFragment(fragment -> {
            try {
                Method drawRoute = MapFragment.class.getDeclaredMethod("drawRoute", List.class);
                drawRoute.setAccessible(true);
                drawRoute.invoke(fragment, points);

                // Możesz sprawdzić czy dodano polyline na mapie
                verify(mockMap).addPolyline(any());
            } catch (Exception e) {
                fail("drawRoute rzuciło wyjątek: " + e.getMessage());
            }
        });
    }
}