package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import org.ros.message.MessageListener;
import topnav_msgs.AngleRangesMsg;

class AngleRangeMessageHandler implements MessageListener<AngleRangesMsg> {

    private WheelsController.IDrivingStrategy drivingStrategy;

    AngleRangeMessageHandler(WheelsController.IDrivingStrategy drivingStrategy) {
        this.drivingStrategy = drivingStrategy;
    }

    @Override
    public void onNewMessage(AngleRangesMsg angleRangesMsg) {
        this.drivingStrategy.handleAngleRangeMessage(angleRangesMsg);
    }
}
