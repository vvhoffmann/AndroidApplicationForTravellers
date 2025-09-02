package pl.vvhoffmann.routemyway;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

import java.util.Objects;

import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;

@RunWith(RobolectricTestRunner.class)
public class RouteMyWayActivityRedirectToastIntegrationTest {

    private RouteMyWayActivity activity;
    private Button btnToApp;

    @Before
    public void setUp() {
        ActivityController<RouteMyWayActivity> controller = Robolectric.buildActivity(RouteMyWayActivity.class);
        activity = controller.create().start().resume().get();
        btnToApp = activity.findViewById(R.id.btnToApp);
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
                Objects.requireNonNull(startedIntent.getComponent()).getClassName());
    }
}