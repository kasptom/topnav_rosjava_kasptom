package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

public interface HeadRotationChangeRequestListener {
    void onRotationChangeRequest(RelativeDirection relativeDirection);
}
