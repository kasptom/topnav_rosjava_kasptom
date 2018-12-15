package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

public class DriveAlongWallStrategy implements WheelsController.IDrivingStrategy {

    private final Log log;
    private WheelsVelocities wheelsVelocities = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
    private WheelsVelocitiesChangeListener listener;

    int lineDetectionThreshold = 5;

    public DriveAlongWallStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        log.info("received config message");
        this.lineDetectionThreshold = configMsg.getLineDetectionThreshold();
        log.info(String.format("line detection threshold changed to: %d", lineDetectionThreshold));
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {


        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {


        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }
}
