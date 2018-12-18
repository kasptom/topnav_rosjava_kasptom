package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import org.ros.message.MessageListener;
import topnav_msgs.HoughAcc;

public class HoughMessageHandler implements MessageListener<HoughAcc> {

    private WheelsController.IDrivingStrategy drivingStrategy;

    HoughMessageHandler(WheelsController.IDrivingStrategy drivingStrategy) {
        this.drivingStrategy = drivingStrategy;
    }

    @Override
    public void onNewMessage(HoughAcc houghAcc) {
        this.drivingStrategy.handleHoughAccMessage(houghAcc);
    }
}
