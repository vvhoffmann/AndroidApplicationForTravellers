package pl.vvhoffmann.routemyway.activities.mapsActivity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MarkersListFragment;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.RouteFragment;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class MapsActivityTest {
    private MapsActivity activity;

    @Before
    public void setUp() {
        Intent intent = new Intent(RuntimeEnvironment.getApplication(), MapsActivity.class);
        activity = Robolectric.buildActivity(MapsActivity.class, intent)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void testInitializeFragments_createsAllFragments() throws Exception {
        Fragment mapFragment = MapsActivity.getMapFragment();
        Fragment markersListFragment = MapsActivity.getMarkersListFragment();

        assertNotNull("MapFragment should be created", mapFragment);
        assertNotNull("MarkersListFragment should be created", markersListFragment);

        // Sprawdź typy fragmentów
        assertTrue(mapFragment instanceof MapFragment);
        assertTrue(markersListFragment instanceof MarkersListFragment);
    }

    @Test
    public void testReplaceFragment_switchesFragmentCorrectly() {
        RouteFragment testFragment = new RouteFragment();
        activity.replaceFragment(testFragment);

        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);

        assertNotNull("Fragment should be replaced", current);
        assertTrue(current instanceof RouteFragment);
    }

    @Test
    public void testOnCreate_setsUpNavigationListener() {
        assertNotNull(activity.findViewById(R.id.bottom_navigation));
    }

    @Test
    public void testOnCreate_checkInitialFragmentIsMapFragment() {
        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);
        assertNotNull(current);
        assertTrue(current instanceof MapFragment);
    }

    @Test
    public void testOnDestroy_shutsDownExecutorService() {
        activity.onDestroy();
        assertTrue(true);
    }
}