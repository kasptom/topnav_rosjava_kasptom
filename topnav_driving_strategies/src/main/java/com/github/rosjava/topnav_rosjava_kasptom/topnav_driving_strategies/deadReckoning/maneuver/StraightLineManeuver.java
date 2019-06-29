package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class StraightLineManeuver implements IManeuver {

    private final double wheelDiamMeters;
    private final long fullRotationTimeMs;
    private final double leftWheelSpeed;
    private final double rightWheelsSpeed;

    private long maneuverDurationMs;

    public StraightLineManeuver(double wheelDiamMeters, long fullRotationTimeMs, double leftWheelSpeed, double rightWheelSpeed) {
        this.wheelDiamMeters = wheelDiamMeters;
        this.fullRotationTimeMs = fullRotationTimeMs;
        this.leftWheelSpeed = leftWheelSpeed;
        this.rightWheelsSpeed = rightWheelSpeed;
    }

    @Override
    public void start(double targetAngleDegrees, double targetDistanceMeters) {
        this.maneuverDurationMs = calculateManeuverTime(targetDistanceMeters);
    }

    @Override
    public WheelsVelocities getNextVelocity(long timeMs) {
        if (isFinished(timeMs)) {
            return ZERO_VELOCITY;
        }
        return new WheelsVelocities(leftWheelSpeed, rightWheelsSpeed, leftWheelSpeed, rightWheelsSpeed);
    }

    @Override
    public boolean isFinished(double timeMs) {
        return maneuverDurationMs < timeMs;
    }

    private long calculateManeuverTime(double targetDistanceMeters) {
        return (long) (fullRotationTimeMs * (targetDistanceMeters / (Math.PI * wheelDiamMeters)));
    }
}
