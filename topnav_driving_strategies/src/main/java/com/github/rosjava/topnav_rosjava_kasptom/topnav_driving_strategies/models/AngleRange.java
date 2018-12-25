package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models;

import topnav_msgs.AngleRangesMsg;

import java.util.ArrayList;
import java.util.List;

public class AngleRange {
    private double angleRad;
    private double range;

    public AngleRange(double angleRad, double range) {
        this.angleRad = angleRad;
        this.range = range;
    }

    public static List<AngleRange> messageToAngleRange(AngleRangesMsg angleRangesMsg) {
        List<AngleRange> angleRanges = new ArrayList<>(angleRangesMsg.getAngles().length);
        double[] angles = angleRangesMsg.getAngles();
        double[] ranges = angleRangesMsg.getDistances();

        for (int i = 0; i < angleRangesMsg.getAngles().length; i++) {
            angleRanges.add(i, new AngleRange(angles[i], ranges[i]));
        }

        return angleRanges;
    }

    public double getAngleRad() {
        return angleRad;
    }

    public double getRange() {
        return range;
    }
}
