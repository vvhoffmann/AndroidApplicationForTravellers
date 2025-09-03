package pl.vvhoffmann.routemyway.utils;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;

public class MarkerUtilsTest {

    @Test
    public void should_return_marker_from_string() {
        //given
        List<LatLng> latLngList = List.of(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219)
        );

        LinkedList<Marker> markersList = IntStream.range(0, latLngList.size())
                .mapToObj(i -> {
                    LatLng latLng = latLngList.get(i);
                    Marker marker = mock(Marker.class);
                    when(marker.getPosition()).thenReturn(latLng);

                    String title = (i == 0) ? "ul. Tamka 35, Warszawa" : "Marker " + i;
                    when(marker.getTitle()).thenReturn(title);

                    return marker;
                })
                .collect(Collectors.toCollection(LinkedList::new));

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));

        String description = "ul. Tamka 35, Warszawa";

        // when
        Marker marker = MarkerUtils.createMarkerFromString(description);

        // then
        assertEquals(markersList.get(0).getPosition(), marker.getPosition());
    }

    @Test
    public void should_return_latlng_list_from_markers_list() {
        //given
        LinkedList<LatLng> latLngList = new LinkedList<>(Arrays.asList(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219)
        ));

        LinkedList<Marker> markersList = new LinkedList<>();
        IntStream.range(0, latLngList.size())
                .mapToObj(i -> {
                    Marker marker = mock(Marker.class);
                    when(marker.getPosition()).thenReturn(latLngList.get(i));
                    when(marker.getTitle()).thenReturn("Marker " + i);
                    return marker;
                })
                .forEach(markersList::add);

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));

        // when
        List<LatLng> latLngFromMarkersList = MarkerUtils.getLatLngFromMarkers(markersList);

        // then
        assertEquals(latLngList, latLngFromMarkersList);
    }

    @Test
    public void should_return_marker_when_latlng_given() {
        //given
        LinkedList<LatLng> latLngList = new LinkedList<>(Arrays.asList(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300),
                new LatLng(52.856614, 21.3522219)
        ));

        LinkedList<Marker> markersList = new LinkedList<>();
        IntStream.range(0, latLngList.size())
                .mapToObj(i -> {
                    Marker marker = mock(Marker.class);
                    when(marker.getPosition()).thenReturn(latLngList.get(i));
                    when(marker.getTitle()).thenReturn("Marker " + i);
                    return marker;
                })
                .forEach(markersList::add);

        MarkersRepository.setInstanceForTests(new MarkersRepositoryTestImpl(markersList));
        LatLng latLng = latLngList.get(0);

        // when
        Marker markerByLatLng = MarkerUtils.getMarkerByLatLng(latLng);

        // then
        assertEquals(markersList.get(0), markerByLatLng);
    }

}