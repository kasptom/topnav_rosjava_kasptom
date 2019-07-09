package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeAlignment;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import java.util.HashMap;

public class ManeuverUtils {

    private static final HashMap<String, Double> relativeAlignmentToMeters = new HashMap<>();
    static {
        relativeAlignmentToMeters.put(RelativeAlignment.CENTER.name(), 0.0);
        relativeAlignmentToMeters.put(RelativeAlignment.LEFT.name(), -1.5 * Limits.OFFSET_MARKER_CENTER_METERS);
        relativeAlignmentToMeters.put(RelativeAlignment.RIGHT.name(), 1.5 * Limits.OFFSET_MARKER_CENTER_METERS);
    }

    public static double relativeAlignmentToMeters(RelativeAlignment alignment) {
        return relativeAlignmentToMeters.get(alignment.name());
    }

    public static double relativeDirectionToDegrees(RelativeDirection direction) {
        return direction.getRotationDegrees();
    }
}
