package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;

/**
 * Rozbudowane testy UI (Espresso) dla MapFragment.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapFragmentUITest {

    private ActivityScenario<MapsActivity> scenario;
    private Activity mActivity;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MapsActivity.class);
        scenario.onActivity(activity -> mActivity = activity);
    }

    /**
     * Sprawdza, czy fragment mapy jest widoczny po starcie.
     */
    @Test
    public void mapFragment_isVisibleOnStart() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Sprawdza, czy przycisk do usuwania markera jest niewidoczny na starcie.
     */
    @Test
    public void removeMarkerButton_isInvisibleOnStart() {
        onView(withId(R.id.btnDeleteMarker)).check(matches(withVisibility(View.INVISIBLE)));
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

    /**
     * Testuje, czy pole autouzupełniania jest widoczne i reaguje na wpisanie tekstu.
     */
    @Test
    public void autocompleteFragment_isVisible_andRespondsToInput() throws InterruptedException {
        // Kliknij w pole wyszukiwarki i wpisz tekst
        onView(withHint("Wyszukaj miejsce")).perform(click(), replaceText("Warszawa"));
        // Odczekaj na podpowiedzi (lepiej użyć IdlingResource, ale na początek sleep wystarczy)
        Thread.sleep(2000);

        // Sprawdź, czy pojawia się podpowiedź zawierająca „Warszawa”
        onView(allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_prediction_primary_text),
                withText("Warszawa"), isDisplayed()))
                .check(matches(isDisplayed()));
    }

}