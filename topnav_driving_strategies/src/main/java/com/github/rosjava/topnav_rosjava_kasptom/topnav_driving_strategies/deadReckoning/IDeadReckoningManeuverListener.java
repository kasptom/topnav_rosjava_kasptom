package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

public interface IDeadReckoningManeuverListener {
    void onManeuverFinished(boolean isWithoutObstacles);
}
