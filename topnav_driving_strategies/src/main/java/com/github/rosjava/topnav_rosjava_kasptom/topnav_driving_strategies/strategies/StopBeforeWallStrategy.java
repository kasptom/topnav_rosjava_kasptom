package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

public class StopBeforeWallStrategy implements WheelsController.IDrivingStrategy {
    private final Log log;
    private WheelsVelocitiesChangeListener listener;

    public StopBeforeWallStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {}

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        // TODO
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        double distance = angleRangesMsg.getDistances()[angleRangesMsg.getDistances().length / 2 + 1];
        log.info(String.format("Number of rays: %d", angleRangesMsg.getAngles().length));
        log.info(String.format("Distance to the front wall [m]: %.2f", distance));

        WheelsVelocities velocities = distance > 1.0 ?
                new WheelsVelocities(2.0, 2.0, 2.0, 2.0) :
                new WheelsVelocities(0.0, 0.0, 0.0, 0.0);

        if (listener != null) {
            listener.onWheelsVelocitiesChanged(velocities);
        }
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }
}
