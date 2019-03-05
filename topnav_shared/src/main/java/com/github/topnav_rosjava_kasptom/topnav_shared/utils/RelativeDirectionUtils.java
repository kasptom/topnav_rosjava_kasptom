package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.google.common.math.DoubleMath;

import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.UNDEFINED;

public class RelativeDirectionUtils {
    static final double ANGLE_TOLERANCE_RADS = 0.01;

    public static RelativeDirection convertMessageToRelativeDirection(std_msgs.String relativeDirection) {
        String directionName = relativeDirection.getData();
        try {
            return RelativeDirection.valueOf(directionName);
        } catch (IllegalArgumentException e) {
            return UNDEFINED;
        }
    }

    public static double toAngleDegrees(RelativeDirection relativeDirection) {
        switch (relativeDirection) {
            case AHEAD:
                return 0.0;
            case AT_LEFT:
                return 90.0;
            case AT_RIGHT:
                return -90.0;
            case BEHIND:
                return -180.0;
            default:
                return 0.0;
        }
    }

    public static double toAngleRadians(RelativeDirection relativeDirection) {
        switch (relativeDirection) {
            case AHEAD:
                return 0.0;
            case AT_LEFT:
                return Math.PI / 2;
            case AT_RIGHT:
                return -Math.PI / 2;
            case BEHIND:
                return -Math.PI;
            default:
                return 0.0;
        }
    }

    public static boolean isInPosition(double jointRotation, RelativeDirection expectedDirection) {
        return DoubleMath.fuzzyEquals(jointRotation, RelativeDirectionUtils.toAngleRadians(expectedDirection), ANGLE_TOLERANCE_RADS);
    }
}
