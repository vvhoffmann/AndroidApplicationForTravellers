package pl.vvhoffmann.routemyway.utils;

import static org.junit.Assert.*;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.List;

public class PolylineUtilsTest {
    @Test
    public void should_decodePolyline_decodes_correctly() {
        //given
        String encoded = "knz}Hi`j_CP\\Zc@JVPYdBaCHOd@jADBFCXXZUhA{A`@p@ITKXa@|Al@wBXw@LWFGv@bCTn@^rAdBjILj@L@b@PPDFXtCuB|AgAh@c@f@w@f@gAJWKVg@fAg@v@i@b@}AfA}@uDaBqESm@sD|DmCpCg@b@_CxBsChCs@p@c@d@{@jAa@n@{@tAS\\Sa@R`@R]z@uA`@o@z@kAeA}Ew@iDdEgGKW[b@Q]";
        //when
        List<LatLng> points = PolylineUtils.decodePolyline(encoded);
        //then
        assertNotNull(points);
        assertTrue(points.size() > 1);
        for (LatLng point : points) assertNotNull(point);
    }
}