package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.List;

public class StopBeforeWallStrategy implements IDrivingStrategy {
    private final Log log;
    private WheelsVelocitiesChangeListener listener;

    public StopBeforeWallStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

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
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {

    }
}
