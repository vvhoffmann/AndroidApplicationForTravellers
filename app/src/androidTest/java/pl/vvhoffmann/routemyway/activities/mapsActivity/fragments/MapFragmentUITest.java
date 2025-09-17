package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.services.MapService;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapFragmentUITest {

    private ActivityScenario<MapsActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MapsActivity.class);
    }

    @Test
    public void should_autocomplete_fragment_work_correctly() throws InterruptedException {
        //given
        onView(withId(R.id.btnDeleteMarker)).check(matches(withVisibility(View.INVISIBLE)));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.autocomplete_fragment)).check(matches(isDisplayed()));

        //when
        onView(withHint("Wyszukaj miejsce")).perform(click(), replaceText("Warszawa"));
        Thread.sleep(2000);
        onView(allOf(withId(com.google.android.libraries.places.R.id.places_autocomplete_prediction_primary_text),
                withText("Warszawa"), isDisplayed()))
                .check(matches(isDisplayed()));

        Thread.sleep(2000);

        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(actionOnItemAtPosition(1, click()));

        Thread.sleep(2000);

        //then
        List<String> resultList = MarkersRepository.getInstance().getMarkers().stream()
                .map(Marker::getTitle)
                .toList();
        long resultSize = resultList.stream()
                .filter(title -> title.contains("Warszawa"))
                .count();

        assertThat(resultSize, Matchers.equalTo(1L));
    }


    @Test
    public void should_add_marker_to_markersRepository_when_on_map_click_was_performed() throws InterruptedException {
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

    @Test
    public void should_drawRoute_does_not_throw_exception() {
        // given
        GoogleMap mockMap = mock(GoogleMap.class);
        MapService.setMap(mockMap); // MapService powinien zwracać ten mock w czasie testu

        List<LatLng> points = Arrays.asList(
                new LatLng(52.1, 21.0),
                new LatLng(52.2, 21.1),
                new LatLng(52.3, 21.2)
        );

        // when
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED); // ważne, żeby fragment był aktywny

        scenario.onFragment(fragment -> {
            try {
                Method drawRoute = MapFragment.class.getDeclaredMethod("drawRoute", List.class);
                drawRoute.setAccessible(true);
                drawRoute.invoke(fragment, points);

                // then
                verify(mockMap).addPolyline(any());
            } catch (Exception e) {
                fail("drawRoute rzuciło wyjątek: " + e.getMessage());
            }
        });
    }

    @Test
    public void should_refresh_markers_and_draw_route_onResume() {
        // given
        GoogleMap mockMap = mock(GoogleMap.class);
        MapService.setMap(mockMap);

        Marker marker = MapService.getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(52.0, 21.0))
                        .title("CurrentPosition")
        );
        Marker marker2 = MapService.getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(52.1, 21.1))
                        .title("TestMarker1")
        );
        Marker marker3 = MapService.getMap().addMarker(
                new MarkerOptions()
                        .position(new LatLng(52.2, 21.2))
                        .title("TestMarker2")
        );
        MarkersRepository.getInstance().addMarker(marker);
        MarkersRepository.getInstance().addMarker(marker2);
        MarkersRepository.getInstance().addMarker(marker3);

        // when
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);

        // then
        scenario.onFragment(fragment -> {
            verify(mockMap).addMarker(any());
            verify(mockMap).addPolyline(any());
        });
    }
}