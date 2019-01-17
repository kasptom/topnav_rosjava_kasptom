package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class RelativeDirectionUtils {
    public static RelativeDirection convertMessageToRelativeDirection(std_msgs.String relativeDirection) {
        String directionName = relativeDirection.getData();
        try {
            return RelativeDirection.valueOf(directionName);
        } catch (IllegalArgumentException e) {
            return UNDEFINED;
        }
    }

    public static double convertRelativeDirectionToAngleDegrees(RelativeDirection relativeDirection) {
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
}
