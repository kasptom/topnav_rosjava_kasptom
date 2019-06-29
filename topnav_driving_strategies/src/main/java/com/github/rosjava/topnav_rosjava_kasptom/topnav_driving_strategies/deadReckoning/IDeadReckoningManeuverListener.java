package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

interface IDeadReckoningManeuverListener {
    void onManeuverFinished(boolean isWithoutObstacles);
}
