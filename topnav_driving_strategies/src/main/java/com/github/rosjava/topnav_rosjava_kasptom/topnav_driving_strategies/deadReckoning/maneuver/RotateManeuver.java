package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class RotateManeuver implements IManeuver {
    private final long fullRobotRotationMilliseconds;
    private final double wheelSpeed;
    private long maneuverDurationMs;
    private WheelsVelocities rotationVelocity;

    public RotateManeuver(long fullRobotRotationMilliseconds, double wheelSpeed) {
        this.fullRobotRotationMilliseconds = fullRobotRotationMilliseconds;
        this.wheelSpeed = wheelSpeed;
    }

    @Override
    public void start(double targetAngleDegrees, double targetDistanceMeters) {
        this.maneuverDurationMs = calculateManeuverTime(targetAngleDegrees);
        System.out.printf("Estimated maneuver time: %.2f[s]\n", maneuverDurationMs / 1000.0);
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
    public boolean isFinished(long timeMs) {
//        System.out.printf("maneuver duration %d, time ms %s\n", maneuverDurationMs, timeMs);
        return maneuverDurationMs < timeMs;
    }

    private long calculateManeuverTime(double targetAngleDegrees) {
        return (long) (fullRobotRotationMilliseconds * Math.abs(targetAngleDegrees) / 360.0);
    }
}
