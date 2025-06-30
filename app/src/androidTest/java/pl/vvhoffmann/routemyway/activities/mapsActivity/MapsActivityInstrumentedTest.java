package pl.vvhoffmann.routemyway.activities.mapsActivity;

import android.Manifest;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import pl.vvhoffmann.routemyway.R;

@RunWith(AndroidJUnit4.class)
public class MapsActivityInstrumentedTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testActivityStartsWithMapFragment() {
        try (ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            scenario.onActivity(activity -> {
                Fragment current = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                assertTrue(current instanceof pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment);
            });
        }
    }
}