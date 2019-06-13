package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

interface IManualSteering {
    WheelsVelocities handleSteeringMessage(short decodedSteering);
}
