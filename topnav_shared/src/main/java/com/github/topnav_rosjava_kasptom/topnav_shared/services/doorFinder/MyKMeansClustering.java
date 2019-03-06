package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import java.util.*;

public class MyKMeansClustering implements IClusteringAlgorithm {

    private final LinkedList<DoorFinder.Point> centroids;
    private final HashMap<DoorFinder.Point, List<DoorFinder.Point>> clusters;
    private final LinkedList<DoorFinder.Point> closePoints;
    private final int meansCount;
    private final int maxIterations;

    public MyKMeansClustering(final int meansCount, final int maxIterations) {
        this.meansCount = meansCount;
        this.maxIterations = maxIterations;

        centroids = new LinkedList<>();
        clusters = new HashMap<>();

        closePoints = new LinkedList<>();
    }

    @Override
    public List<List<DoorFinder.Point>> computeClusters(List<DoorFinder.Point> data) {
        if (data.size() <= meansCount) {
            ArrayList<List<DoorFinder.Point>> angleRangeClusters = new ArrayList<>();
            angleRangeClusters.add(data);
            return angleRangeClusters;
        }

        closePoints.clear();
        closePoints.addAll(data);

        resetMeans(closePoints);
        for (int i = 0; i < maxIterations; i++) {
            recalculateMeans(closePoints);
        }

        ArrayList<List<DoorFinder.Point>> angleRangeClusters = new ArrayList<>();
        angleRangeClusters.add(clusters.get(centroids.get(0)));
        angleRangeClusters.add(clusters.get(centroids.get(1)));

        return angleRangeClusters;
    }

    private void resetMeans(LinkedList<DoorFinder.Point> closePoints) {
        centroids.clear();
        centroids.add(closePoints.get(0));
        centroids.add(closePoints.get(closePoints.size() - 1));
    }

    private void recalculateMeans(List<DoorFinder.Point> closePoints) {
        clusters.clear();
        centroids.forEach(centroid -> clusters.put(centroid, new ArrayList<>()));

        closePoints.forEach(point -> {
            DoorFinder.Point closestCentroid = centroids.stream()
                    .min(Comparator.comparingDouble(centroid -> distanceTo(point, centroid)))
                    .orElse(null);
            clusters.get(closestCentroid).add(point);
        });

        centroids.clear();
        clusters.values().forEach(cluster -> {
            DoorFinder.Point newCentroid = cluster.stream().reduce((point, point2) -> {
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

    private double distanceTo(DoorFinder.Point first, DoorFinder.Point second) {
        return Math.sqrt(Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2));
    }

    public DoorFinder.Point getClustersMidPoint() {
        List<DoorFinder.Point> firstCluster = this.clusters.get(centroids.get(0));
        List<DoorFinder.Point> secondCluster = this.clusters.get(centroids.get(1));
        final DoorFinder.Point[] firstClosest = {null};
        final DoorFinder.Point[] secondClosest = {null};
        final double[] minDistance = {Double.POSITIVE_INFINITY};
        final double[] distance = {0};
        firstCluster.forEach(point -> secondCluster.forEach(otherPoint -> {
            distance[0] = distanceTo(point, otherPoint);
            if (distance[0] < minDistance[0]) {
                minDistance[0] = distance[0];
                firstClosest[0] = point;
                secondClosest[0] = otherPoint;
            }
        }));

        return new DoorFinder.Point((firstClosest[0].x + secondClosest[0].x) / 2, (firstClosest[0].y + secondClosest[0].y) / 2);
    }
}
