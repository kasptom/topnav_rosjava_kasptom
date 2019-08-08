package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import std_msgs.UInt64;
import topnav_msgs.AngleRangesMsg;

public interface IDeadReckoningDrive {

    void setWheelsParameters(double axisLength, double wheelDiameter, long fullRobotRotationMilliseconds);

    void setManeuverFinishListener(IDeadReckoningManeuverListener listener);

    void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener);

    void startManeuver(String maneuverName, double angleDegrees, double distanceMeters);

    void onAngleRangeMessage(AngleRangesMsg angleRangesMsg);

    void onTickerMessage(UInt64 tickerMessage);
}
