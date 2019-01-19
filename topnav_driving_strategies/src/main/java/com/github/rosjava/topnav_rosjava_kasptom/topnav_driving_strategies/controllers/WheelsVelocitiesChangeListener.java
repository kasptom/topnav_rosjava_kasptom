package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

public interface WheelsVelocitiesChangeListener {
    void onWheelsVelocitiesChanged(final WheelsVelocities velocities);
}