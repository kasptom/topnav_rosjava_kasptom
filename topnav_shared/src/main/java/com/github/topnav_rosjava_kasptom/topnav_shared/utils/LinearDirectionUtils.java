package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.google.common.math.DoubleMath;

import static com.github.topnav_rosjava_kasptom.topnav_shared.utils.RelativeDirectionUtils.ANGLE_TOLERANCE_RADS;

public class LinearDirectionUtils {
    public static boolean isInPosition(double jointRotation, double requestedDirectionDegrees) {
        return DoubleMath.fuzzyEquals(jointRotation, requestedDirectionDegrees * Math.PI / 180.0, ANGLE_TOLERANCE_RADS);
    }
}
