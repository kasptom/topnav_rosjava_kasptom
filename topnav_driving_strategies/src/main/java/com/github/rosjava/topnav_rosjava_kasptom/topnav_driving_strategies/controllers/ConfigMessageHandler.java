package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import org.ros.message.MessageListener;
import topnav_msgs.TopNavConfigMsg;

class ConfigMessageHandler implements MessageListener<TopNavConfigMsg> {
    private WheelsController.IDrivingStrategy drivingStrategy;

    ConfigMessageHandler(WheelsController.IDrivingStrategy drivingStrategy) {
        this.drivingStrategy = drivingStrategy;
    }


    @Override
    public void onNewMessage(TopNavConfigMsg configMsg) {
        this.drivingStrategy.handleConfigMessage(configMsg);
    }
}
