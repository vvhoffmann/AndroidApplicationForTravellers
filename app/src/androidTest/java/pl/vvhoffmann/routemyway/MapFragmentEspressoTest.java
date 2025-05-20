package pl.vvhoffmann.routemyway;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.not;

import android.Manifest;

import androidx.test.rule.GrantPermissionRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapFragmentEspressoTest {

    @Rule
    public ActivityTestRule<RouteMyWayActivity> activityRule =
            new ActivityTestRule<>(RouteMyWayActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testMapFragmentLoads() {
        // Check that the map fragment loads properly
        onView(withId(R.id.map)).check(matches(not(withText(""))));
    }

    @Test
    public void testAddMarkerOnMapClick() {
        // Perform a click action on the map
        onView(withId(R.id.map)).perform(click());

        // Check that a marker is added
        // (This requires custom matcher logic for Google Maps markers, which is not shown here)
    }

    @Test
    public void testRemoveMarkerButtonVisibility() {
        // Simulate a marker click
        onView(withId(R.id.map)).perform(click());
        onView(withId(R.id.btnDeleteMarker)).check(matches(not(withText(""))));
    }

    @Test
    public void testRedirectToGoogleMaps() {
        // Simulate a click on the redirect button
        onView(withId(R.id.btnRedirectToGoogleMaps)).perform(click());

        // Check that the appropriate intent is triggered
        // (This requires intent verification via Espresso Intents)
    }
}