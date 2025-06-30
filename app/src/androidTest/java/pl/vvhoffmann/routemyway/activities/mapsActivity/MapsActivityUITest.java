package pl.vvhoffmann.routemyway.activities.mapsActivity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.vvhoffmann.routemyway.R;

@RunWith(AndroidJUnit4.class)
public class MapsActivityUITest {

    @Test
    public void testBottomNavigationSwitchesFragments() {
        ActivityScenario.launch(MapsActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.bottom_navigation))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));


        Espresso.onView(ViewMatchers.withId(R.id.nav_list))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.listView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));


        Espresso.onView(ViewMatchers.withId(R.id.nav_route))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.list_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));


        Espresso.onView(ViewMatchers.withId(R.id.nav_info))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.tvInfoAuthor))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}