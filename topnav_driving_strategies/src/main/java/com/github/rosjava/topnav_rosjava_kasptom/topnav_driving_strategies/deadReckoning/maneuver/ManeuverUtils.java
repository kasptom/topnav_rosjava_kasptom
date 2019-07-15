package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeAlignment;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import java.util.HashMap;

public class ManeuverUtils {

    private static final HashMap<String, Double> relativeAlignmentToMeters = new HashMap<>();
    static {
        relativeAlignmentToMeters.put(RelativeAlignment.CENTER.name(), 0.0);
        relativeAlignmentToMeters.put(RelativeAlignment.LEFT.name(), Limits.ACCORDING_TO_MARKER_CENTER_OFFSET);
        relativeAlignmentToMeters.put(RelativeAlignment.RIGHT.name(), -Limits.ACCORDING_TO_MARKER_CENTER_OFFSET);
    }

    public static double relativeAlignmentToMeters(RelativeAlignment alignment) {
        return relativeAlignmentToMeters.get(alignment.name());
    }

    public static double relativeDirectionToDegreesWithIgnoringOffset(RelativeDirection targetDirection, double dstX, double dstY) {
        return ManeuverUtils.relativeDirectionToDegrees(targetDirection)
                + (90.0 - Math.atan2(dstY, dstX) * 180.0 / Math.PI);
    }

    private static double relativeDirectionToDegrees(RelativeDirection direction) {
        return direction.getRotationDegrees();
    }
}
