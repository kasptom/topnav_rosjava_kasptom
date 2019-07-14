package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class StraightLineManeuver implements IManeuver {

    private final double axisLength;
    private final double wheelDiameter;
    private final long fullRotationTimeMs;
    private final double leftWheelSpeed;
    private final double rightWheelsSpeed;

    private long maneuverDurationMs;

    public StraightLineManeuver(double axisLength, double wheelDiameter, long fullRotationTimeMs, double leftWheelSpeed, double rightWheelSpeed) {
        this.axisLength = axisLength;
        this.wheelDiameter = wheelDiameter;
        this.fullRotationTimeMs = fullRotationTimeMs;
        this.leftWheelSpeed = leftWheelSpeed;
        this.rightWheelsSpeed = rightWheelSpeed;
    }

    @Override
    public void start(double targetAngleDegrees, double targetDistanceMeters) {
        this.maneuverDurationMs = calculateManeuverTime(targetDistanceMeters);
        System.out.printf("Estimated maneuver time: %.2f[s]\n", maneuverDurationMs / 1000.0);
    }

    @Override
    public WheelsVelocities getNextVelocity(long timeMs) {
        if (isFinished(timeMs)) {
            return ZERO_VELOCITY;
        }
        return new WheelsVelocities(leftWheelSpeed, rightWheelsSpeed, leftWheelSpeed, rightWheelsSpeed);
    }

    @Override
    public boolean isFinished(long timeMs) {
        return maneuverDurationMs < timeMs;
    }

    private long calculateManeuverTime(double targetDistanceMeters) {
        double fullWheelRotationDistance =  Math.PI * wheelDiameter;
        double fullRobotRotationDistance = Math.PI * axisLength;
        double fullWheelRotationTimeMs = fullRotationTimeMs * fullWheelRotationDistance / fullRobotRotationDistance;

        return (long) (fullWheelRotationTimeMs * targetDistanceMeters / fullWheelRotationDistance);
    }
}
