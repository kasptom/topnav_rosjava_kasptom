package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import topnav_msgs.AngleRangesMsg;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.DOOR_DETECTION_RANGE;

public class DoorFinder {

    private final IClusteringAlgorithm algorithm;

    public DoorFinder() {
        this.algorithm = new MyKMeansClustering(2, 6);
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

    public Point getClustersMidPoint() {
        return algorithm.getClustersMidPoint();
    }

    public static class Point {
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
