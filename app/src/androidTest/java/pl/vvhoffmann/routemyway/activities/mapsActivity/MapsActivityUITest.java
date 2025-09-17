package pl.vvhoffmann.routemyway.activities.mapsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static org.junit.Assert.assertTrue;

import android.Manifest;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.vvhoffmann.routemyway.R;

@RunWith(AndroidJUnit4.class)
public class MapsActivityUITest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void should_bottom_navigation_switches_fragments_correctly() {
        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);

        scenario.onActivity(activity -> {
                Fragment current = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                assertTrue(current instanceof pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment);
            });

        onView(ViewMatchers.withId(R.id.bottom_navigation)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.autocomplete_fragment)).check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.nav_list)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.listView)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvClickOnElementInfo)).check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.nav_route)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.list_view)).check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.nav_info)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tvInfoTitle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoContent)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvPrivacyPolicy)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvTermsOfService)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoGoogle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoAuthor)).check(matches(ViewMatchers.isDisplayed()));
    }
}