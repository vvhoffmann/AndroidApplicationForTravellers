package pl.vvhoffmann.routemyway;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class RouteMyWayActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<RouteMyWayActivity> activityRule =
            new ActivityScenarioRule<>(RouteMyWayActivity.class);

    @Test
    public void testInitializeUIComponents_assignsViewsCorrectly() {
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvDesc)).check(matches(isDisplayed()));
        onView(withId(R.id.btnToApp)).check(matches(isDisplayed()));
        onView(withId(R.id.switchLocation)).check(matches(isDisplayed()));
    }

    @Test
    public void testBtnToAppClickOpensMapsActivity() {
        onView(withId(R.id.btnToApp)).perform(click());
        // Możesz sprawdzić czy pojawił się nowy ekran, np. przez obecność elementu z MapsActivity
    }
}