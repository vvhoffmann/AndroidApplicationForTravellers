package pl.vvhoffmann.routemyway;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RouteMyWayActivityUITest {

    @Rule public ActivityScenarioRule<RouteMyWayActivity> activityRule =
            new ActivityScenarioRule<>(RouteMyWayActivity.class);

    @Test
    public void testRouteMyWayActivityUI() {
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.btnToApp)).check(matches(isDisplayed()));
        onView(withId(R.id.switchLocation)).check(matches(isDisplayed()));

        RouteMyWayActivity.locationEnabled = true;

        onView(withId(R.id.switchLocation)).perform(click());

        activityRule.getScenario().onActivity(activity -> assertFalse(RouteMyWayActivity.locationEnabled));

        onView(withId(R.id.switchLocation)).perform(click());

        activityRule.getScenario().onActivity(activity -> assertTrue(RouteMyWayActivity.locationEnabled));

        onView(withId(R.id.btnToApp)).perform(click());

        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}