package pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of the Graham scan algorithm to compute the convex hull
 * of a set of 2D points in O(n log n) time.
 */
public class GrahamAlgorithm {

    public static List<LatLng> getConvexHull(List<LatLng> inputPoints) {
        if(inputPoints.size() < 3)
            return inputPoints;

        List<LatLng> points = new ArrayList<>(inputPoints);

        //wybieram pierwszy wierzchołek, posiadający jak najmniejszą współrzędną y i x
        LatLng pivot = getLowestLatLng(points);
        //sortuje wg kata względem pierwszego wierzchołka i osi OX rosnąco
        sortByAngle(points, pivot);

        Stack<LatLng> stack = buildConvexHull(points);

        return finalizeConvexHull(stack);
    }

    public static LatLng getLowestLatLng(List<LatLng> points)
    {
        return points.stream()
                .min(pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm.PointUtils::min)
                .orElseThrow(() -> new IllegalArgumentException("LatLng list cannot be empty"));
    }

    private static void sortByAngle(List<LatLng> points, LatLng pivot) {
        points.sort((p1, p2) -> {
            //punkt startowy musi być na początku, więc szukamy go
            if (p1 == pivot) return -1;
            if (p2 == pivot) return 1;

            int newPointDirection = countPointDirection(pivot, p1, p2);

            if (newPointDirection == 0) { //Dla punktów współliniowych
                double dist1 = PointUtils.distance(pivot, p1);
                double dist2 = PointUtils.distance(pivot, p2);
                return Double.compare(dist1, dist2);
            }

            return -newPointDirection;
        });
    }

    private static Stack<LatLng> buildConvexHull(List<LatLng> sortedPoints) {
        Stack<LatLng> stack = new Stack<>();
        // na stos wkładamy pierwszy punkt
        stack.push(sortedPoints.get(0));
        // na stos wkładamy punkt, który będziemy sprawdzać
        stack.push(sortedPoints.get(1));

        for (int i = 2, size = sortedPoints.size(); i < size; i++) {
            LatLng next = sortedPoints.get(i);
            LatLng top = stack.pop();

            // usuwamy punkt, ponieważ idzie zgodnie z kierunkiem zegara
            while (!stack.isEmpty() && countPointDirection(top, next, stack.peek()) < 0)
                top = stack.pop();

            stack.push(top);
            stack.push(next);
        }
        return stack;
    }

    private static ArrayList<LatLng> finalizeConvexHull(Stack<LatLng> stack) {
        ArrayList<LatLng> result = new ArrayList<>(stack);
        result.add(result.get(0));
        result.remove(result.get(0));
        Collections.reverse(result);

        return result;
    }

    public static int countPointDirection(LatLng a, LatLng b, LatLng r)
    {
        return (int)Math.signum((b.latitude - a.latitude) * (r.longitude - b.longitude) - (b.longitude - a.longitude) * (r.latitude - b.latitude));
    }

}