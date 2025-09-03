package pl.vvhoffmann.routemyway.utils;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.repositories.MarkersRepositoryTestImpl;

public class PlacesUtilsTest {

    @Test
    public void should_return_marker_from_string() throws IOException {
        //given
        List<LatLng> latLngList = List.of(
                new LatLng(52.2296756, 21.0122287),
                new LatLng(52.8919300, 21.5113300)
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

        Geocoder geocoder = mock(Geocoder.class);
        String mockedResponse = "Address[addressLines=[0:\"Solec 58/60, 00-382 Warszawa, Polska\"],feature=58/60,admin=Województwo mazowieckie,sub-admin=Powiat Warszawa,locality=Warszawa,thoroughfare=Solec,postalCode=00-382,countryCode=PL,countryName=Polska,hasLatitude=true,latitude=52.237012199999995,hasLongitude=true,longitude=21.0286769,phone=null,url=null,extras=null]";
        List<Address> mockedAddressList = new LinkedList<>();
        Address mockedAddress = mock(Address.class);
        when(mockedAddress.getFeatureName()).thenReturn("58/60");
        when(mockedAddress.getThoroughfare()).thenReturn("Solec");
        when(mockedAddress.getSubThoroughfare()).thenReturn("58/60");
        when(mockedAddress.getLocality()).thenReturn("Warszawa");
        when(mockedAddress.getAdminArea()).thenReturn("Województwo mazowieckie");
        when(mockedAddress.getSubAdminArea()).thenReturn("Powiat Warszawa");
        when(mockedAddress.getCountryName()).thenReturn("Polska");
        when(mockedAddress.getCountryCode()).thenReturn("PL");
        when(mockedAddress.getLatitude()).thenReturn(52.237012199999995);
        when(mockedAddress.getLongitude()).thenReturn(21.0286769);
        mockedAddressList.add(mockedAddress);
        when(geocoder.getFromLocation(latLngList.get(0).latitude, latLngList.get(0).longitude, 1)).thenReturn(mockedAddressList);

        // when
        String placeDescription = PlacesUtils.getPlaceDescription(latLngList.get(0), geocoder);

        // then
        String expectedDescription = "ul. Solec 58/60, Warszawa";
        assertEquals(expectedDescription, placeDescription);
    }

}