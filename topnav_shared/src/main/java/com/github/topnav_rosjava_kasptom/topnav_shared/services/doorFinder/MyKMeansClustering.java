package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import java.util.*;

public class MyKMeansClustering implements IClusteringAlgorithm {

    private final LinkedList<DoorFinder.Point> centroids;
    private final HashMap<DoorFinder.Point, List<DoorFinder.Point>> clusters;
    private final LinkedList<DoorFinder.Point> closePoints;
    private final int meansCount;
    private final int maxIterations;

    MyKMeansClustering(final int meansCount, final int maxIterations) {
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

        return DoorFinder.Point.toClustersList(clusters, centroids);
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
                    .min(Comparator.comparingDouble(centroid -> DoorFinder.Point.distanceTo(point, centroid)))
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

    public DoorFinder.Point getClustersMidPoint() {
        return DoorFinder.Point.getMidPoint(clusters, centroids);
    }

    @Override
    public List<DoorFinder.Point> getMidPointWithClosest() {
        return DoorFinder.Point.getMidPointWithClosest(clusters, centroids);
    }
}
