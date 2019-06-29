package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

public class AroundCircleManeuver implements IManeuver {
    public AroundCircleManeuver(double wheelDiameter, long fullWheelRotationTimeMs) {
    }

    @Override
    public void start(double targetAngleDegrees, double targetDistanceMeters) {

    }

    @Override
    public WheelsVelocities getNextVelocity(long timeMs) {
        return null;
    }

    @Override
    public boolean isFinished(double timeMs) {
        return false;
    }
}
