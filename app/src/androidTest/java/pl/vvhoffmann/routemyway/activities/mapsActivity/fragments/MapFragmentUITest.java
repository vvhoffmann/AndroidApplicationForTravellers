package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapFragmentUITest {

    private ActivityScenario<MapsActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MapsActivity.class);
    }

    @Test
    public void mapFragment_should_work_correctly() throws InterruptedException {
        onView(withId(R.id.btnDeleteMarker)).check(matches(withVisibility(View.INVISIBLE)));

        onView(withHint("Wyszukaj miejsce")).perform(click(), replaceText("Warszawa"));
        Thread.sleep(2000);
        onView(allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_prediction_primary_text),
                withText("Warszawa"), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    @Test
    public void should_add_marker_to_markersrepository_when_on_map_click_was_performed() throws InterruptedException {
        onView(withId(R.id.btnDeleteMarker)).check(matches(withVisibility(View.INVISIBLE)));

        onView(withHint("Wyszukaj miejsce")).perform(click(), replaceText("Warszawa"));
        Thread.sleep(2000);
        onView(allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_prediction_primary_text),
                withText("Warszawa"), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    public static Matcher<View> withVisibility(final int visibility) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with visibility: " + visibility);
            }

            @Override
            public boolean matchesSafely(View view) {
                return view.getVisibility() == visibility;
            }
        };
    }
}