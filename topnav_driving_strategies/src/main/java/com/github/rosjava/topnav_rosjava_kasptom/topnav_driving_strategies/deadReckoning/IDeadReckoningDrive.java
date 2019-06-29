package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import topnav_msgs.AngleRangesMsg;

public interface IDeadReckoningDrive {

    void setWheelsParameters(double axisLength, double wheelDiameter, double fullWheelRotationTime);

    void moveForward(double estimatedDistanceMeters);

    void moveBackward(double estimatedDistanceMeters);

    /**
     * @param estimatedAngleDegrees rotate counterclockwise if more than zero else clockwise
     */
    void rotateChassis(double estimatedAngleDegrees);

    /**
     * @param estimatedRadiusMeters estimated radius of the circle
     * @param estimatedAngleDegrees estimated sector size, counterclockwise if more than zero else clockwise
     *                              allowed values (0°, 360°]
     */
    void driveAroundCircle(double estimatedRadiusMeters, double estimatedAngleDegrees);

    void onAngleRangeMessage(AngleRangesMsg angleRangesMsg);

    void setManeuverFinishListener(IDeadReckoningManeuverListener listener);

    void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener);
}
