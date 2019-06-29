package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class RotateManeuver implements IManeuver {
    private final double axisLength;
    private final long fullWheelRotationTimeMs;
    private final double wheelDiameter;
    private final double wheelSpeed;
    private long maneuverDurationMs;
    private WheelsVelocities rotationVelocity;

    public RotateManeuver(double axisLength, double wheelDiameter, long fullWheelRotationTimeMs, double wheelSpeed) {
        this.axisLength = axisLength;
        this.wheelDiameter = wheelDiameter;
        this.fullWheelRotationTimeMs = fullWheelRotationTimeMs;
        this.wheelSpeed = wheelSpeed;
    }

    @Override
    public void start(double targetAngleDegrees, double targetDistanceMeters) {
        this.maneuverDurationMs = calculateManeuverTime(targetAngleDegrees);
        this.rotationVelocity = getRotationVelocity(targetAngleDegrees);
    }

    private WheelsVelocities getRotationVelocity(double targetAngleDegrees) {
        return targetAngleDegrees > 0
                ? new WheelsVelocities(-wheelSpeed, wheelSpeed, -wheelSpeed, wheelSpeed)
                : new WheelsVelocities(wheelSpeed, -wheelSpeed, wheelSpeed, -wheelSpeed);
    }

    @Override
    public WheelsVelocities getNextVelocity(long timeMs) {
        if (isFinished(timeMs)) {
            return ZERO_VELOCITY;
        }

        return rotationVelocity;
    }

    @Override
    public boolean isFinished(double timeMs) {
        return maneuverDurationMs < timeMs;
    }

    private long calculateManeuverTime(double targetAngleDegrees) {
        double fullRotationDistance = axisLength * Math.PI;
        double rotationDistance = Math.abs(targetAngleDegrees) / 360.0 * fullRotationDistance;
        double wheelDistance = rotationDistance / 2.0;

        return (long) (fullWheelRotationTimeMs * (wheelDistance / (Math.PI * wheelDiameter)));
    }
}
