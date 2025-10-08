package pl.vvhoffmann.routemyway;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapsActivityUITest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            );

    private ActivityScenario<MapsActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MapsActivity.class);
    }

    @Test
    public void should_all_fragments_flow_on_MapActivity_work_correctly() throws InterruptedException {
        //1. autocomplete fragment works correctly when I try to find few places in row
        //given
        onView(withId(R.id.btnDeleteMarker)).check(matches(withVisibility(View.INVISIBLE)));
        onView(ViewMatchers.withId(R.id.bottom_navigation)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.autocomplete_fragment)).check(matches(ViewMatchers.isDisplayed()));

        List<String> initialMarkersTitles = Arrays.asList("Charlotte plac Zbawiciela", "Syrenka", "Kolumna Zygmunta III Wazy", "Teatr Syrena");

        //when
        for (String title : initialMarkersTitles) {
            onView(withHint("Wyszukaj miejsce")).perform(click(), replaceText(title));
            Thread.sleep(2000);
            onView(
                    allOf(
                            withId(com.google.android.libraries.places.R.id.places_autocomplete_prediction_primary_text),
                            withText(title),
                            isDisplayed()
                    )
            ).perform(click());
            Thread.sleep(2000);
        }

        //then
        assertThat(MarkersRepository.getInstance().getMarkers().size(), Matchers.equalTo(5));

        //2. all added markers appear on the list view in ListFragment
        //given & when
        onView(ViewMatchers.withId(R.id.nav_list)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.listView)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvClickOnElementInfo)).check(matches(ViewMatchers.isDisplayed()));

        //then
        onView(withId(R.id.listView))
                .check(new MapsActivityUITest.ListViewItemCountAssertion(5));

        //3. marker can be deleted from the list and in the same time from markers repository
        //given & when
        onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(1)
                .perform(click());

        onView(ViewMatchers.withId(R.id.btnRemoveMarker)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.btnRemoveMarker)).perform(click());

        //then
        assertThat(MarkersRepository.getInstance().getMarkers().size(), Matchers.equalTo(4));
        onView(withId(R.id.listView))
                .check(new MapsActivityUITest.ListViewItemCountAssertion(4));

        //4. the route is being calculated on RouteFragment
        //given && when && then
        onView(ViewMatchers.withId(R.id.nav_route)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tvTitle)).check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.list_view)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.listView))
                .check(new MapsActivityUITest.ListViewItemCountAssertion(5));

        onView(ViewMatchers.withId(R.id.tvResultTitle)).check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.tvResultDescription)).check(matches(ViewMatchers.isDisplayed()));
        AtomicReference<String> ref = new AtomicReference<>();
        onView(withId(R.id.tvResultDescription))
                .check((v, e) -> {
                    if (e != null) throw e;
                    TextView tv = (TextView) v;
                    ref.set(tv.getText().toString().trim());
                });
        String numberOnly = ref.get()
                .replaceAll("[^0-9.,]", "")
                .replace(",", ".");
        double value = Double.parseDouble(numberOnly);

        assertTrue("Expected value > 0" + value, value > 0.0);

        //5. button "Edytuj Punkty" on RouteFragment redirects correctly into ListFragment
        //given & when
        onView(withId(R.id.btnEditPoints)).perform(click());

        //then
        onView(ViewMatchers.withId(R.id.listView)).check(matches(ViewMatchers.isDisplayed()));

        //6. button "Pokaż Mapę" on RouteFragment redirects correctly into MapFragment
        //given
        onView(ViewMatchers.withId(R.id.nav_route)).perform(ViewActions.click());

        //when
        onView(withId(R.id.btnShowMap)).perform(click());

        //then
        onView(ViewMatchers.withId(R.id.map)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.btnRedirectToGoogleMaps)).check(matches(ViewMatchers.isDisplayed()));

        //7. InfoFragment is displayed correctly
        //given & when & then
        onView(ViewMatchers.withId(R.id.nav_info)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tvInfoTitle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoContent)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvPrivacyPolicy)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvTermsOfService)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoGoogle)).check(matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.tvInfoAuthor)).check(matches(ViewMatchers.isDisplayed()));
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

    public class ListViewItemCountAssertion implements ViewAssertion {

        private final int expectedCount;

        public ListViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            if (!(view instanceof ListView)) {
                throw new AssertionError("View is not a ListView");
            }

            ListView listView = (ListView) view;
            int actualCount = listView.getAdapter().getCount();
            assertEquals("ListView item count", expectedCount, actualCount);
        }
    }
}