package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import topnav_msgs.*;

import java.util.List;

public interface IDrivingStrategy {
    void startStrategy();

    void handleConfigMessage(TopNavConfigMsg configMsg);

    void handleHoughAccMessage(HoughAcc houghAcc);

    void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg);

    void handleDetectionMessage(FeedbackMsg feedbackMsg);

    void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg);

    void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener);

    void setHeadRotationChangeListener(HeadRotationChangeListener listener);

    void setStrategyFinishedListener(StrategyFinishedListener listener);

    void setGuidelineParameters(List<String> parameters);
}