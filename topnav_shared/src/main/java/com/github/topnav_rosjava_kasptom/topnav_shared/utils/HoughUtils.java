package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder.DoorFinder;
import topnav_msgs.HoughAcc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HoughUtils {
    public static List<HoughCell> toList(HoughAcc houghAcc) {
        List<HoughCell> houghCells = new ArrayList<>();

        int angleSamples = houghAcc.getAccumulator().get(0).getAccRow().length;
        int rangeSamples = houghAcc.getAccumulator().size();

        /* TODO pass it in the HoughAcc
         tnavbot_ws/src/topnav_ros_kasptom/topnav_bot_description/urdf/capo/head/hokuyo.gazebo */
        double minAngle = 0;
        double maxAngle = 2 * Math.PI;
        double angleStep = (maxAngle - minAngle) / angleSamples;

        double minRange = 0.20;
        double maxRange = 5.0;
        double rangeStep = (maxRange - minRange) / rangeSamples;

        double range;
        double angle;

        for (int rangeIdx = 0; rangeIdx < rangeSamples; rangeIdx++) {
            range = minRange + rangeIdx * rangeStep;
            int[] votes = houghAcc.getAccumulator().get(rangeIdx).getAccRow();
            for (int angleIdx = 0; angleIdx < angleSamples; angleIdx++) {
                angle = minAngle + angleIdx * angleStep;
                houghCells.add(new HoughCell(angle, range, votes[angleIdx]));
            }
        }

        return houghCells;
    }

    public static List<HoughCell> toFilteredList(HoughAcc houghAcc, int lineDetectionThreshold) {
        List<HoughCell> houghCells = HoughUtils.toList(houghAcc);
        return houghCells.stream()
                .filter(cell -> cell.getVotes() >= lineDetectionThreshold)
                .collect(Collectors.toList());
    }

    public static HoughCell toHoughCell(DoorFinder.Point firstPoint, DoorFinder.Point secondPoint) {
        double x1 = firstPoint.getX();
        double y1 = firstPoint.getY();
        double x2 = secondPoint.getX();
        double y2 = secondPoint.getY();


        double const_cross = (x1 * y2 - x2 * y1) / ((y2 - y1) * (y2 - y1) + (x1 - x2) * (x1 - x2));
        double x_cross =  (x1 - x2) * const_cross;
        double y_cross = -(y2 - y1) * const_cross;

        double angle = Math.atan2(y_cross, x_cross);
        angle = angle > 3/2.0 * Math.PI
                ? angle - 3 /2.0 * Math.PI
                : angle + Math.PI / 2.0;


        return new HoughCell(angle, Math.sqrt(x_cross * x_cross + y_cross * y_cross), 2);
    }
}
