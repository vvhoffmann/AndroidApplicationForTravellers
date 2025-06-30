package pl.vvhoffmann.routemyway;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

public class RouteMyWayActivityUnitTest {

    @After
    public void tearDown() {
        RouteMyWayActivity.locationEnabled = true;
    }

    @Test
    public void testDefaultValueIsTrue() {
        assertTrue("Flaga locationEnabled powinna być domyślnie true", RouteMyWayActivity.locationEnabled);
    }

    @Test
    public void testSetFalseThenTrue() {
        RouteMyWayActivity.locationEnabled = false;
        assertFalse("Flaga locationEnabled powinna być false po ustawieniu", RouteMyWayActivity.locationEnabled);

        RouteMyWayActivity.locationEnabled = true;
        assertTrue("Flaga locationEnabled powinna być true po ponownym ustawieniu", RouteMyWayActivity.locationEnabled);
    }

}