package pl.vvhoffmann.routemyway.utils.quasioptimalalgorithm;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.utils.RouteUtils;

public class QuasiOptimizationAlgorithm {
    private static List<LatLng> convexHull;
    private static List<LatLng> resultPath;
    public static List<LatLng> inputPoints;
    private static double distance;

    public static List<LatLng> getTSPSolution() {
        resultPath = new ArrayList<>();
        inputPoints = MarkersRepository.getInstance().getLatLngList();
        if (inputPoints == null)
            return new ArrayList<>();

        if (inputPoints.size() < Constants.MIN_POINTS_TO_RUN_ALGORITHM)
            return inputPoints;

        buildConvexHull(inputPoints);
        insertNotAssignedPointsIntoPath(getNotAssignedPointsList());

        generateResultPathFromCurrentPosition();
        resultPath = returnRoundPath();
        setDistance();

        return resultPath;
    }

    private static void generateResultPathFromCurrentPosition() {
        LatLng currentPosition = MarkersRepository.getInstance().getCurrentPositionMarker().getPosition();
        List<LatLng> newResultPath = new ArrayList<>();
        int currentPositionId = resultPath.indexOf(currentPosition);
        for (int i = currentPositionId; i < resultPath.size(); ++i)
        {
            newResultPath.add(resultPath.get(i));
        }
        for (int i = 0; i < currentPositionId; ++i)
        {
            newResultPath.add(resultPath.get(i));
        }
        resultPath = newResultPath;
    }

    private static void buildConvexHull(List<LatLng> inputPoints) {
        convexHull = GrahamAlgorithm.getConvexHull(inputPoints);
        resultPath.addAll(convexHull);
    }

    private static ArrayList<LatLng> getNotAssignedPointsList() {
        ArrayList<LatLng> notAssignedPoints = new ArrayList<>(inputPoints);
        notAssignedPoints.removeAll(convexHull);

        return notAssignedPoints;
    }

    private static void insertNotAssignedPointsIntoPath(ArrayList<LatLng> inputPointsToInsert) {
        while (!inputPointsToInsert.isEmpty()) {
            int selectedPointIndex = 0;
            int bestInsertIndex = Integer.MIN_VALUE;
            double minimalDistance = Constants.MAX_VALUE;

            for (int p = 0; p < inputPointsToInsert.size(); ++p) {
                LatLng pointToInsert = inputPointsToInsert.get(p);
                InsertionResult insertionResult = findBestInsertionForPoint(pointToInsert);

                if (minimalDistance > insertionResult.distance) {
                    minimalDistance = insertionResult.distance;
                    bestInsertIndex = insertionResult.insertIndex;
                    selectedPointIndex = p;
                }
            }
            LatLng selectedPoint = inputPointsToInsert.remove(selectedPointIndex);
            addTargetPointIntoResult(bestInsertIndex, selectedPoint);
        }
    }

    private static InsertionResult findBestInsertionForPoint(LatLng pointToInsert) {
        double bestDistance = Constants.MAX_VALUE;
        int bestInsertIndex = -1;

        for (int p = 0; p < resultPath.size(); ++p) {
            //result LatLng future location : segmentStart - pointToInsert - segmentEnd
            int previousIndex = (p == 0) ? resultPath.size() - 1 : p - 1;
            LatLng segmentStart = resultPath.get(previousIndex);
            LatLng segmentEnd = resultPath.get(p);

            LatLng projectionPoint = PointUtils.projection(segmentStart, segmentEnd, pointToInsert);
            double segmentLength = PointUtils.distance(segmentStart, segmentEnd);
            double aToProjectionLength = PointUtils.distance(segmentStart, projectionPoint);
            double bToProjectionLength = PointUtils.distance(segmentEnd, projectionPoint);
            double pointToProjectionLength = PointUtils.distance(projectionPoint, pointToInsert);

            int insertionIndex = p;

            if (aToProjectionLength + bToProjectionLength > segmentLength) {
                insertionIndex = findBetterProjectionAlternative(pointToInsert, segmentStart, segmentEnd, pointToProjectionLength, p, previousIndex);
                pointToProjectionLength = estimateOffSegmentPenalty(pointToInsert, segmentStart, segmentEnd);
            }

            if (pointToProjectionLength < bestDistance) {
                bestDistance = pointToProjectionLength;
                bestInsertIndex = insertionIndex;
            }
        }
        return new InsertionResult(bestInsertIndex, bestDistance);
    }

    private static int findBetterProjectionAlternative(LatLng pointToInsert, LatLng segmentStart, LatLng segmentEnd, double pointToProjetionLength, int currentIndex, int previousIndex) {
        double distanceFromPointToStart = PointUtils.distance(segmentStart, pointToInsert);
        double distanceFromPointToEnd = PointUtils.distance(segmentEnd, pointToInsert);

        if (distanceFromPointToStart < distanceFromPointToEnd) {
            int candidateIndex = (previousIndex == 0) ? resultPath.size() - 1 : previousIndex - 1;
            LatLng candidateStart = resultPath.get(candidateIndex);
            LatLng candidateProjectionPoint = PointUtils.projection(candidateStart, segmentStart, pointToInsert);
            if (PointUtils.distance(pointToInsert, candidateProjectionPoint) > pointToProjetionLength)
                return candidateIndex;
        } else {
            int candidateIndex = (currentIndex == resultPath.size() - 1) ? 0 : currentIndex + 1;
            LatLng candidateEnd = resultPath.get(candidateIndex);
            LatLng candidateProjectionPoint = PointUtils.projection(segmentEnd, candidateEnd, pointToInsert);
            if (PointUtils.distance(pointToInsert, candidateProjectionPoint) > pointToProjetionLength)
                return candidateIndex;
        }

        return currentIndex;
    }

    private static double estimateOffSegmentPenalty(LatLng pointToInsert, LatLng segmentStart, LatLng segmentEnd) {
        return Math.min(2 * PointUtils.distance(segmentStart, pointToInsert), 2 * PointUtils.distance(segmentEnd, pointToInsert));
    }

    private static void addTargetPointIntoResult(int index, LatLng latLng) {
        resultPath.add(index, latLng);
    }

    public static double getDistance() {
        return distance;
    }

    private static void setDistance() {
        distance = RouteUtils.getRouteLength(resultPath);
    }

    public static List<LatLng> returnRoundPath() {
        resultPath.add(resultPath.get(0));
        return resultPath;
    }

    private static class InsertionResult {
        int insertIndex;
        double distance;

        InsertionResult(int insertIndex, double distance) {
            this.insertIndex = insertIndex;
            this.distance = distance;
        }
    }
}