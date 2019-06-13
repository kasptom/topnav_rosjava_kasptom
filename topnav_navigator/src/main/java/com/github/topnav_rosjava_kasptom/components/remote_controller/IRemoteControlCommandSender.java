package com.github.topnav_rosjava_kasptom.components.remote_controller;

public interface IRemoteControlCommandSender {
    void driveForward();
    void driveBackward();
    void turnLeft();
    void turnRight();
    void stopDrivingForward();
    void stopDrivingBackward();
    void stopTurningLeft();
    void stopTurningRight();
}
