package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

public interface IClockMessageHandler {
    void handleClockMessage(std_msgs.UInt64 clockMsg);
}
