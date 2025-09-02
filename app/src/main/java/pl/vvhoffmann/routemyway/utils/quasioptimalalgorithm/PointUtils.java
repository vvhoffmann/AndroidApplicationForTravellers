package pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm;

import com.google.android.gms.maps.model.LatLng;

public class PointUtils {

    public static LatLng projection(LatLng a, LatLng b, LatLng c) {
        double BAy = b.latitude - a.latitude, BAx = b.longitude - a.longitude, CAy = c.latitude - a.latitude, CAx = c.longitude - a.longitude;
        double BAy2 = BAy * BAy, BAx2 = BAx * BAx, BA2 = BAy2 + BAx2;
        return new LatLng(
                (a.longitude * BAy2 + CAy * BAx * BAy + c.longitude * BAx2) / BA2,
                (a.latitude * BAx2 + CAx * BAy * BAx + c.latitude * BAy2) / BA2
        );
    }

    public static boolean less(LatLng a, LatLng b) {
        return ((a.latitude < b.latitude) || ((a.latitude == b.latitude) && (a.longitude < b.longitude)));
    }

    public static int min(LatLng a, LatLng b) {
        if (less(a, b)) return -1;
        if (less(b, a)) return +1;
        return 0;
    }

    public static double distance(LatLng a, LatLng b) {
        return Math.hypot(a.longitude - b.longitude, a.latitude - b.latitude);
    }
}
