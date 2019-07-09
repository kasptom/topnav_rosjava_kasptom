package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.ManeuverDescription;

import java.util.Queue;

public interface IManeuverDescriptionGenerator {
    /**
     * Generates maneuvers to reach the estimated target position from the source position
     * @param srcX robot's x coordinate according to ArUco marker's coordinates
     * @param srcY robot's y coordinate according to ArUco marker's coordinates
     * @param srcRotation head rotation's angle for which the camera is centered on the ArUco marker
     * @param dstX robot's estimated x coordinate after executing all of the generated maneuvers
     * @param dstY robot's estimated y coordinate after executing all of the generated maneuvers
     * @param dstRotation estimated head rotation's angle for which the camera should be centered on the ArUco marker
     * @return queue of maneuvers to execute sequentially in order to reach the (dstX, dstY, dstRotation) position
     */
    Queue<ManeuverDescription> generateManeuverDescriptions(double srcX, double srcY, double srcRotation, double dstX, double dstY, double dstRotation);
}
