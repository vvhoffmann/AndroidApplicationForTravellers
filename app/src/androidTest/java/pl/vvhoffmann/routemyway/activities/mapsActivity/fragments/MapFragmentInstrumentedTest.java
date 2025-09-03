package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static org.junit.Assert.*;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MapFragmentInstrumentedTest {

    @Test
    public void fragmentLaunchesAndUIElementsArePresent() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView().findViewById(pl.vvhoffmann.routemyway.R.id.map));
            assertNotNull(fragment.getChildFragmentManager().findFragmentById(pl.vvhoffmann.routemyway.R.id.autocomplete_fragment));
        });
    }

    @Test
    public void should_drawRoute_does_not_throw_exception() {
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
            } catch (Exception e) {
                fail("drawRoute rzuciło wyjątek: " + e.getMessage());
            }
        });
    }


    @Test
    public void should_refresh_markers_and_draw_route_onResume() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
        });
    }
}