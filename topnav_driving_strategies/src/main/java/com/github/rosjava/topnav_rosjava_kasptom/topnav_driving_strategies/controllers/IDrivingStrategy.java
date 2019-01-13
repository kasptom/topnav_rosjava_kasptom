package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

public interface IDrivingStrategy {
    void handleConfigMessage(TopNavConfigMsg configMsg);

    void handleHoughAccMessage(HoughAcc houghAcc);

    void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg);

    void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener);

    void setHeadRotationListener(HeadRotationListener listener);
}