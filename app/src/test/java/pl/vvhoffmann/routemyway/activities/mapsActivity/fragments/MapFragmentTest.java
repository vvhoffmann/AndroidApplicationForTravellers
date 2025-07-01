package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static org.junit.Assert.*;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import java.util.List;

import pl.vvhoffmann.routemyway.utils.PolylineUtils;

/**
 * Jednostkowy test dekodowania polilinii.
 */
public class MapFragmentTest {
    @Test
    public void decodePolyline_decodesCorrectly() {
        // Przykładowa zakodowana polilinia
        String encoded = "knz}Hi`j_CP\\Zc@JVPYdBaCHOd@jADBFCXXZUhA{A`@p@ITKXa@|Al@wBXw@LWFGv@bCTn@^rAdBjILj@L@b@PPDFXtCuB|AgAh@c@f@w@f@gAJWKVg@fAg@v@i@b@}AfA}@uDaBqESm@sD|DmCpCg@b@_CxBsChCs@p@c@d@{@jAa@n@{@tAS\\Sa@R`@R]z@uA`@o@z@kAeA}Ew@iDdEgGKW[b@Q]";
        List<LatLng> points = PolylineUtils.decodePolyline(encoded);
        assertNotNull(points);
        assertTrue(points.size() > 1); // Spodziewamy się kilku punktów
        for (LatLng point : points) assertNotNull(point);
    }
}