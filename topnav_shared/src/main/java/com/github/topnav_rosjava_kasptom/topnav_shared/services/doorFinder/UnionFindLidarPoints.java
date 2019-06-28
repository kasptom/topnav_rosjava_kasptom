package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import com.github.topnav_rosjava_kasptom.topnav_shared.utils.QuickUnionFind;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class UnionFindLidarPoints implements IClusteringAlgorithm {
    private static final double NEAR_POINTS_GAP_METERS = 0.3;

    private final ArrayList<DoorFinder.Point> centroids;
    private final HashMap<DoorFinder.Point, List<DoorFinder.Point>> clusters;

    private HashMap<Integer, DoorFinder.Point> idToPoint;
    private HashMap<DoorFinder.Point, Integer> pointToId;
    private QuickUnionFind unionFind;

    UnionFindLidarPoints() {
        idToPoint = new HashMap<>();
        pointToId = new HashMap<>();
        clusters = new HashMap<>();
        centroids = new ArrayList<>();
    }

    @Override
    public List<List<DoorFinder.Point>> computeClusters(List<DoorFinder.Point> points) {
        unionFind = new QuickUnionFind(points.size());
        reset(points);

        boolean isShrinking = true;
        while (isShrinking) {
            isShrinking = false;
            for (Integer pointId : idToPoint.keySet()) {
                for (Integer otherId : idToPoint.keySet()) {
                    if (pointId.equals(otherId)) continue;

                    DoorFinder.Point point = idToPoint.get(pointId);

                    if (unionFind.find(pointId, otherId)) continue;

                    List<DoorFinder.Point> otherPointSiblings = points
                            .stream()
                            .filter(p -> unionFind.find(pointToId.get(p), otherId))
                            .collect(Collectors.toList());

                    for (DoorFinder.Point sibling : otherPointSiblings) {
                        if (DoorFinder.Point.distanceTo(point, sibling) <= NEAR_POINTS_GAP_METERS) {
                            unionFind.union(pointToId.get(point), pointToId.get(sibling));
                            isShrinking = true;
                        }
                    }
                }
            }
        }

        HashSet<Integer> uniqueParents = new HashSet<>();
        int[] parentIds = unionFind.getParentIds();
        Arrays.stream(parentIds).forEach(uniqueParents::add);
        if (uniqueParents.size() < 2) {
            return Collections.emptyList();
        } else if (uniqueParents.size() > 2) {
            uniqueParents = keepTwoClosestParents(uniqueParents);
        }

        uniqueParents.forEach(parent -> {
            centroids.add(idToPoint.get(parent));
            clusters.put(idToPoint.get(parent), points.stream().filter(point -> parentIds[pointToId.get(point) - 1] == parent).collect(Collectors.toList()));
        });

        return DoorFinder.Point.toClustersList(clusters, centroids);
    }

    @Override
    public DoorFinder.Point getClustersMidPoint() {
        return DoorFinder.Point.getMidPoint(clusters, centroids);
    }

    @Override
    public List<DoorFinder.Point> getMidPointWithClosest() {
        return DoorFinder.Point.getMidPointWithClosest(clusters, centroids);
    }

    private void reset(List<DoorFinder.Point> points) {
        idToPoint.clear();
        pointToId.clear();
        clusters.clear();
        centroids.clear();

        int pointId = 1;
        for (DoorFinder.Point point : points) {
            idToPoint.put(pointId, point);
            pointToId.put(point, pointId);
            pointId++;
        }
    }

    private HashSet<Integer> keepTwoClosestParents(HashSet<Integer> uniqueParents) {
        List<DoorFinder.Point> points = uniqueParents
                .stream()
                .map(parent -> idToPoint.get(parent))
                .collect(Collectors.toList());
        DoorFinder.Point origin = new DoorFinder.Point(0.0, 0.0);

        points.sort(Comparator.comparingDouble(point -> DoorFinder.Point.distanceTo(origin, point)));
        points = points.subList(0,2);
        return points.stream()
                .map(point -> pointToId.get(point))
                .collect(toCollection(HashSet::new));
    }
}
