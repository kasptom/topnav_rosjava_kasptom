package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

public interface IManeuver {
    void start(double targetAngleDegrees, double targetDistanceMeters);

    WheelsVelocities getNextVelocity(long timeMs);

    boolean isFinished(long timeMs);
}
