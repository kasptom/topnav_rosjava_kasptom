package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import java.util.List;

public interface IClusteringAlgorithm {

    /**
     * Divides the data into the M clusters
     * @param data points as N x 2 array
     * @return clusters as M element list of points
     */
    List<List<DoorFinder.Point>> computeClusters(List<DoorFinder.Point> data);

    DoorFinder.Point getClustersMidPoint();

    List<DoorFinder.Point> getMidPointWithClosest();
}
