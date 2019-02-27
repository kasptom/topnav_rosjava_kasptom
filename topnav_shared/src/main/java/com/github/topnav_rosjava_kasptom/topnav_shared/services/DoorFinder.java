package com.github.topnav_rosjava_kasptom.topnav_shared.services;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import topnav_msgs.AngleRangesMsg;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.DOOR_DETECTION_RANGE;

public class DoorFinder {
    private final int MEANS_COUNT = 2;
    private final LinkedList<Point> centroids;
    private final HashMap<Point, List<Point>> clusters;
    private final HashMap<AngleRange, Point> lidarToPoint;
    private final HashMap<Point, AngleRange> pointToLidar;
    private final LinkedList<Point> closePoints;

    public DoorFinder() {
        centroids = new LinkedList<>();
        clusters = new HashMap<>();

        lidarToPoint = new HashMap<>();
        pointToLidar = new HashMap<>();
        closePoints = new LinkedList<>();
    }

    /**
     * Using k = 2 means to group the close points (points withing {@link Limits}' DOOR_DETECTION_RANGE)
     *
     * @param angleRangesMsg
     * @return
     */
    public List<List<AngleRange>> dividePointsToClusters(AngleRangesMsg angleRangesMsg) {
        int maxIterations = 6;

        List<AngleRange> angleRanges = AngleRange.messageToAngleRange(angleRangesMsg);

        List<AngleRange> closeAngleRanges = angleRanges.stream()
                .filter(point -> point.getRange() <= DOOR_DETECTION_RANGE)
                .collect(Collectors.toList());

        if (closeAngleRanges.size() <= MEANS_COUNT) {
            ArrayList<List<AngleRange>> angleRangeClusters = new ArrayList<>();
            angleRangeClusters.add(closeAngleRanges);
            return angleRangeClusters;
        }

        lidarToPoint.clear();
        pointToLidar.clear();
        closePoints.clear();
        closeAngleRanges.forEach(angleRange -> {
            Point point = new Point(angleRange);
            lidarToPoint.put(angleRange, point);
            pointToLidar.put(point, angleRange);
            closePoints.add(point);
        });

        resetMeans(closePoints);
        for (int i = 0; i < maxIterations; i++) {
            recalculateMeans(closePoints);
        }

        ArrayList<List<AngleRange>> angleRangeClusters = new ArrayList<>();
        angleRangeClusters.add(clusters.get(centroids.get(0)).stream().map(pointToLidar::get).collect(Collectors.toList()));
        angleRangeClusters.add(clusters.get(centroids.get(1)).stream().map(pointToLidar::get).collect(Collectors.toList()));
        return angleRangeClusters;
    }


    public Point getClustersMidPoint() {
        List<Point> firstCluster = this.clusters.get(centroids.get(0));
        List<Point> secondCluster = this.clusters.get(centroids.get(1));
        final Point[] firstClosest = {null};
        final Point[] secondClosest = {null};
        final double[] minDistance = {Double.POSITIVE_INFINITY};
        final double[] distance = {0};
        firstCluster.forEach(point -> {
            secondCluster.forEach(otherPoint -> {
                distance[0] = distanceTo(point, otherPoint);
                if (distance[0] < minDistance[0]) {
                    minDistance[0] = distance[0];
                    firstClosest[0] = point;
                    secondClosest[0] = otherPoint;
                }
            });
        });

        return new Point((firstClosest[0].x + secondClosest[0].x)/2, (firstClosest[0].y + secondClosest[0].y)/2);
    }

    private void resetMeans(LinkedList<Point> closePoints) {
        centroids.clear();
        centroids.add(closePoints.get(0));
        centroids.add(closePoints.get(closePoints.size() - 1));
    }

    private void recalculateMeans(List<Point> closePoints) {
        clusters.clear();
        centroids.forEach(centroid -> clusters.put(centroid, new ArrayList<>()));

        closePoints.forEach(point -> {
            Point closestCentroid = centroids.stream()
                    .min(Comparator.comparingDouble(centroid -> distanceTo(point, centroid)))
                    .orElse(null);
            clusters.get(closestCentroid).add(point);
        });

        centroids.clear();
        clusters.values().forEach(cluster -> {
            Point newCentroid = cluster.stream().reduce((point, point2) -> {
                point.x += point2.x;
                point.y += point2.y;
                return point;
            }).map(point -> {
                point.x /= cluster.size();
                point.y /= cluster.size();
                return point;
            }).get();
            centroids.add(newCentroid);
        });
    }

    private double distanceTo(Point first, Point second) {
        return Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
    }

    public class Point {
        double x, y;

        Point(AngleRange angleRange) {
            x = angleRange.getRange() * Math.sin(angleRange.getAngleRad());
            y = angleRange.getRange() * Math.cos(angleRange.getAngleRad());
        }

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

}
