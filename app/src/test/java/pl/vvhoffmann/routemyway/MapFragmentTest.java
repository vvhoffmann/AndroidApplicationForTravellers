package pl.vvhoffmann.routemyway;

import static org.mockito.Mockito.*;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.services.MapService;

import java.util.LinkedList;
import java.util.List;

public class MapFragmentTest {

    @Mock
    private GoogleMap mockGoogleMap;

    @Mock
    private Context mockContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        MapService.setMap(mockGoogleMap);
    }

    @Test
    public void testRefreshMapMarkers() {
        // Arrange
        LinkedList<LatLng> mockLatLngList = new LinkedList<>();
        mockLatLngList.add(new LatLng(52.2297, 21.0122));
        mockLatLngList.add(new LatLng(50.0647, 19.9450));
        when(MarkersRepository.getLatLngList()).thenReturn(mockLatLngList);

        // Act
        MapFragment.refreshMapMarkers(null); // Geocoder is null in this test

        // Assert
        verify(mockGoogleMap, times(2)).addMarker(any());
    }

    @Test
    public void testOnRequestPermissionsResult_Granted() {
        // Arrange
        doNothing().when(mockGoogleMap).setMyLocationEnabled(true);

        // Act
        MapFragment fragment = new MapFragment();
        fragment.onRequestPermissionsResult(1, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_GRANTED});

        // Assert
        verify(mockGoogleMap, times(1)).setMyLocationEnabled(true);
    }

    @Test
    public void testOnRequestPermissionsResult_Denied() {
        // Arrange
        MapFragment fragment = new MapFragment();

        // Act
        fragment.onRequestPermissionsResult(1, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                new int[]{PackageManager.PERMISSION_DENIED});

        // Assert
        // Expect no interaction with GoogleMap
        verify(mockGoogleMap, never()).setMyLocationEnabled(true);
    }

    @Test
    public void testDecodePolyline() {
        // Arrange
        String encodedPolyline = "u{~vFvyys@_Cn@}BlC";

        // Act
        List<LatLng> result = new MapFragment().decodePolyline(encodedPolyline);

        // Assert
        assert !result.isEmpty();
    }
}