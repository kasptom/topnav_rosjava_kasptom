package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import topnav_msgs.MarkersMsg;

public interface ArUcoMessageListener {
    void handleArUcoMessage(MarkersMsg message);
}
