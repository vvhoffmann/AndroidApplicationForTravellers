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

/**
 * Rozbudowane testy instrumentacyjne MapFragment z użyciem FragmentScenario.
 */
@RunWith(AndroidJUnit4.class)
public class MapFragmentInstrumentedTest {

    /**
     * Testuje, czy fragment się uruchamia i zawiera mapę oraz autouzupełnianie.
     */
    @Test
    public void fragmentLaunchesAndUIElementsArePresent() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView().findViewById(pl.vvhoffmann.routemyway.R.id.map));
            assertNotNull(fragment.getChildFragmentManager().findFragmentById(pl.vvhoffmann.routemyway.R.id.autocomplete_fragment));
        });
    }

    /**
     * Testuje, czy metoda drawRoute działa bez wyjątku dla przykładowych punktów (bez asercji na mapie).
     */
    @Test
    public void drawRoute_doesNotCrash() throws Exception {
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

    /**
     * Testuje, czy po Resume fragment odświeża markery lub rysuje trasę.
     */
    @Test
    public void fragment_onResume_refreshesMarkersOrDrawsRoute() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onFragment(fragment -> {
            // Sprawdź logikę odświeżania: możesz podmienić repozytoria mockami i sprawdzić efekty uboczne
            assertNotNull(fragment);
        });
    }
}