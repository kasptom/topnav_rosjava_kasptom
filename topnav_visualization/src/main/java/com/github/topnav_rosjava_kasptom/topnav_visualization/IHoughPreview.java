package com.github.topnav_rosjava_kasptom.topnav_visualization;

import sensor_msgs.LaserScan;

public interface IHoughPreview {
    void onLaserPointsUpdated(LaserScan scanMsg);
}
