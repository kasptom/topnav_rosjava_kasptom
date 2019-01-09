package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;

public interface WheelsVelocitiesChangeListener {
    void onWheelsVelocitiesChanged(final WheelsVelocities velocities);
}