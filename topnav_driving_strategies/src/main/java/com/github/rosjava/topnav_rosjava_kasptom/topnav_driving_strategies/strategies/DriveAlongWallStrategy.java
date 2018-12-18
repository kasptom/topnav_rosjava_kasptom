package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.HoughAccRow;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DriveAlongWallStrategy implements WheelsController.IDrivingStrategy {

    private final Log log;
    private WheelsVelocities wheelsVelocities = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
    private WheelsVelocitiesChangeListener listener;

    int lineDetectionThreshold = 5;

    int messageCounter = 0;
    long timeStamp;

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
        refreshRateCheck();

        List<HoughAccRow> houghAccRows = houghAcc.getAccumulator()
                .stream()
                .filter(row -> Arrays.stream(row.getAccRow()).filter(vote -> vote > lineDetectionThreshold).count() > 0)
                .collect(Collectors.toList());

        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) { }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }

    private void refreshRateCheck() {
        if (messageCounter == 0) {
            timeStamp = System.nanoTime();
        }

        this.messageCounter++;

        if (messageCounter == 40) {
            String timeInfo = String.format("received 40 messages within approx: %.3f [s]", (System.nanoTime() - timeStamp) / 1e9);
            log.info(timeInfo);
            messageCounter = 0;
        }
    }
}
