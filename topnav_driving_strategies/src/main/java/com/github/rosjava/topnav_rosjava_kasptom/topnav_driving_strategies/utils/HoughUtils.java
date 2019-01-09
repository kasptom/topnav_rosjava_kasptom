package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.HoughCell;
import topnav_msgs.HoughAcc;

import java.util.ArrayList;
import java.util.List;

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
}
