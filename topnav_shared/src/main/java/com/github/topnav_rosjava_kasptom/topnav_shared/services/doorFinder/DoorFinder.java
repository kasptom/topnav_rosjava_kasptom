package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.exceptions.PointNotFoundException;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import topnav_msgs.AngleRangesMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.DOOR_DETECTION_RANGE;

public class DoorFinder {

    private final IClusteringAlgorithm algorithm;

    public DoorFinder() {
//        this.algorithm = new MyKMeansClustering(2, 6);
        this.algorithm = new UnionFindLidarPoints();
//        this.algorithm = new ExpectationsMaximizationAdapter();
    }

    /**
     * Using the {@link IClusteringAlgorithm} close points (points withing {@link Limits}' DOOR_DETECTION_RANGE)
     *
     * @param angleRangesMsg LIDAR angle ranges
     * @return clusters
     */
    public List<List<Point>> dividePointsToClusters(AngleRangesMsg angleRangesMsg) {
        List<AngleRange> angleRanges = AngleRange.messageToAngleRange(angleRangesMsg);

        List<AngleRange> closeAngleRanges = angleRanges.stream()
                .filter(point -> point.getRange() <= DOOR_DETECTION_RANGE)
                .collect(Collectors.toList());

        List<Point> closeAngleRangesPoints = closeAngleRanges.stream()
                .map(Point::new)
                .collect(Collectors.toList());

        return algorithm.computeClusters(closeAngleRangesPoints);
    }

    public Point getClustersMidPoint() throws PointNotFoundException {
        Point point = algorithm.getClustersMidPoint();
        if (point == null || point.getX() == Double.POSITIVE_INFINITY) {
            throw new PointNotFoundException("could not found the mid point");
        }
        return point;
    }

    /**
     * @return mid point and the closest point from each of the found clusters
     */
    public List<Point> getMidPointWithClosestClustersPoints() {
        List<Point> midPointWithClosest = algorithm.getMidPointWithClosest();
        if (midPointWithClosest.isEmpty()) {
            throw new PointNotFoundException("could not find the mid point");
        }
        return midPointWithClosest;
    }

    public static class Point {
        double x, y;

        Point(AngleRange angleRange) {
            x = angleRange.getRange() * Math.sin(angleRange.getAngleRad());
            y = angleRange.getRange() * Math.cos(angleRange.getAngleRad());
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        static double distanceTo(DoorFinder.Point first, DoorFinder.Point second) {
            return Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
        }

        static Point getMidPoint(HashMap<Point, List<Point>> clusters, List<Point> centroids) {

            if (clusters.size() != 2 || centroids.size() != 2) {
                return new DoorFinder.Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            }

            final DoorFinder.Point[] firstClosest = {null};
            final DoorFinder.Point[] secondClosest = {null};
            findClosestPointsFromDifferentClusters(clusters, centroids, firstClosest, secondClosest);

            return new DoorFinder.Point((firstClosest[0].x + secondClosest[0].x) / 2, (firstClosest[0].y + secondClosest[0].y) / 2);
        }

        static List<Point> getMidPointWithClosest(HashMap<Point, List<Point>> clusters, List<Point> centroids) {

            List<Point> midWithClosest = new ArrayList<>();

            if (clusters.size() != 2 || centroids.size() != 2) {
                return midWithClosest;
            }

            final DoorFinder.Point[] firstClosest = {null};
            final DoorFinder.Point[] secondClosest = {null};
            findClosestPointsFromDifferentClusters(clusters, centroids, firstClosest, secondClosest);

            Point midPoint = new DoorFinder.Point((firstClosest[0].x + secondClosest[0].x) / 2, (firstClosest[0].y + secondClosest[0].y) / 2);

            midWithClosest.add(midPoint);
            midWithClosest.add(firstClosest[0]);
            midWithClosest.add(secondClosest[0]);
            return midWithClosest;
        }

        static ArrayList<List<Point>> toClustersList(HashMap<Point, List<Point>> clusters, List<Point> centroids) {
            ArrayList<List<DoorFinder.Point>> pointClusters = new ArrayList<>();
            if (clusters.size() != 2 || centroids.size() != 2) {
                return pointClusters;
            }
            pointClusters.add(clusters.get(centroids.get(0)));
            pointClusters.add(clusters.get(centroids.get(1)));
            return pointClusters;
        }

        private static void findClosestPointsFromDifferentClusters(HashMap<Point, List<Point>> clusters, List<Point> centroids, Point[] firstClosest, Point[] secondClosest) {
            List<DoorFinder.Point> firstCluster = clusters.get(centroids.get(0));
            List<DoorFinder.Point> secondCluster = clusters.get(centroids.get(1));

            final double[] minDistance = {Double.POSITIVE_INFINITY};
            final double[] distance = {0};
            firstCluster.forEach(point -> secondCluster.forEach(otherPoint -> {
                distance[0] = Point.distanceTo(point, otherPoint);
                if (distance[0] < minDistance[0]) {
                    minDistance[0] = distance[0];
                    firstClosest[0] = point;
                    secondClosest[0] = otherPoint;
                }
            }));
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
