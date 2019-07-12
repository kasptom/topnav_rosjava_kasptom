package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.ManeuverDescription;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.VALUE_MANEUVER_NAME_FORWARD;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.VALUE_MANEUVER_NAME_ROTATE;

public class ManeuverDescriptionGenerator implements IManeuverDescriptionGenerator {

    @Override
    public Queue<ManeuverDescription> generateManeuverDescriptions(double srcX, double srcY, double srcRotation, double dstX, double dstY, double dstRotation) {
        Queue<ManeuverDescription> descriptions = new ArrayDeque<>(3);

        double firstRotationAngle = getFirstRotationAngle(srcX, srcY, dstX, dstY, srcRotation);
        double driveForwardDistance = getDriveForwardDistance(srcX, srcY, dstX, dstY);
        double secondRotationAngle = getSecondRotationAngle(srcX, srcY, firstRotationAngle, dstRotation);

        descriptions.add(new ManeuverDescription(VALUE_MANEUVER_NAME_ROTATE, firstRotationAngle, 0.0));
        descriptions.add(new ManeuverDescription(VALUE_MANEUVER_NAME_FORWARD, 0.0, driveForwardDistance));
        descriptions.add(new ManeuverDescription(VALUE_MANEUVER_NAME_ROTATE, secondRotationAngle, 0.0));

        return descriptions;
    }

    private double getFirstRotationAngle(double srcX, double srcY, double dstX, double dstY, double srcRotation) {
        double distance = getDriveForwardDistance(srcX, srcY, dstX, dstY);
        double markerSourceDistance = Math.sqrt(Math.pow(srcX, 2) + Math.pow(srcY, 2));
        double markerTargetDistance = Math.sqrt(Math.pow(dstX, 2) + Math.pow(dstY, 2));
        double markerDestinationAngleDiff = getMarkerDestinationAngleDiff(markerTargetDistance, distance, markerSourceDistance);
        return srcRotation - markerDestinationAngleDiff;
    }

    private double getDriveForwardDistance(double srcX, double srcY, double dstX, double dstY) {
        return Math.sqrt(Math.pow(dstX - srcX, 2) + Math.pow(dstY - srcY, 2));
    }

    private double getSecondRotationAngle(double srcX, double srcY, double firstRotationAngle, double dstRotation) {
        double theta = Math.atan(srcX / srcY);
        return firstRotationAngle + theta + dstRotation;
    }

    private double getMarkerDestinationAngleDiff(double markerTargetDistance, double distance, double markerSourceDistance) {
        return Math.acos(
                (Math.pow(distance, 2) + Math.pow(markerSourceDistance, 2) - Math.pow(markerTargetDistance, 2)) /
                        (2 * distance * markerSourceDistance)
        );
    }
}
