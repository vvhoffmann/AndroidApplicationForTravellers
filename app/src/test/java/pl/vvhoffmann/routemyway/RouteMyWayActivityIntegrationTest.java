package pl.vvhoffmann.routemyway;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;

@RunWith(RobolectricTestRunner.class)
public class RouteMyWayActivityIntegrationTest {

    private RouteMyWayActivity activity;
    private TextView title;
    private TextView description;
    private Button btnToApp;
    private Switch switchLocation;

    @Before
    public void setUp() {
        ActivityController<RouteMyWayActivity> controller = Robolectric.buildActivity(RouteMyWayActivity.class);
        activity = controller.create().start().resume().get();

        title = activity.findViewById(R.id.tvTitle);
        description = activity.findViewById(R.id.tvDesc);
        btnToApp = activity.findViewById(R.id.btnToApp);
        switchLocation = activity.findViewById(R.id.switchLocation);
    }

    @Test
    public void testUIComponentsAreInitialized() {
        assertNotNull("Title TextView should be initialized", title);
        assertNotNull("Description TextView should be initialized", description);
        assertNotNull("Button should be initialized", btnToApp);
        assertNotNull("Switch should be initialized", switchLocation);
    }

    @Test
    public void testSwitchLocationToggleUpdatesFlag() {
        assertTrue(activity.locationEnabled);
        assertTrue(switchLocation.isChecked());

        switchLocation.setChecked(false);
        assertFalse(activity.locationEnabled);

        switchLocation.setChecked(true);
        assertTrue(activity.locationEnabled);
    }

    @Test
    public void testButtonClickShowsToastAndStartsMapsActivity() {
        btnToApp.performClick();

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Witaj w aplikacji!!", latestToast);

        Intent startedIntent = shadowOf(activity).getNextStartedActivity();
        assertNotNull("Intent should not be null", startedIntent);
        assertEquals("Should launch MapsActivity",
                MapsActivity.class.getName(),
                startedIntent.getComponent().getClassName());
    }
}
