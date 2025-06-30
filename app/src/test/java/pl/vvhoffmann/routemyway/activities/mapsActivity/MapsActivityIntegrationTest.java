package pl.vvhoffmann.routemyway.activities.mapsActivity;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import pl.vvhoffmann.routemyway.R;

@RunWith(RobolectricTestRunner.class)
public class MapsActivityIntegrationTest {
    private MapsActivity activity;

    @Before
    public void setUp() {
        ActivityController<MapsActivity> controller = Robolectric.buildActivity(MapsActivity.class);
        activity = controller.create().start().resume().get();
    }

    @Test
    public void testDefaultFragmentIsMapFragment() {
        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);
        assertNotNull(current);
        assertTrue(current instanceof pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment);
    }

    @Test
    public void testNavigationToMarkersListFragment() {
        BottomNavigationView nav = activity.findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_list);

        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);
        assertTrue(current instanceof pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MarkersListFragment);
    }
}
