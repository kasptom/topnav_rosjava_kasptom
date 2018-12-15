package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import models.WheelsVelocities;

public interface WheelsVelocitiesChangeListener {
    void onWheelsVelocitiesChanged(WheelsVelocities velocities);
}