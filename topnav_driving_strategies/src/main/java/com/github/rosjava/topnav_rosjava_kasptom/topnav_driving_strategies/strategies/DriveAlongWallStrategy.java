package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils.HoughUtils;
import models.HoughCell;
import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DriveAlongWallStrategy implements WheelsController.IDrivingStrategy {

    private final Log log;
    private static final WheelsVelocities ZERO_VELOCITY = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
    private double TOO_CLOSE_RANGE = 0.3;

    private WheelsVelocities wheelsVelocities = ZERO_VELOCITY;
    private WheelsVelocitiesChangeListener listener;

    private int lineDetectionThreshold = 5;

    private int messageCounter = 0;
    private long timeStamp;

    private boolean isObstacleToClose;

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

        List<HoughCell> houghCells = HoughUtils.toList(houghAcc);

        houghCells
                .stream()
                .filter(cell -> cell.getVotes() >= this.lineDetectionThreshold)
                .forEach(cell -> log.info(String.format(
                        "detected line: (dst, ang, vts) = (%.2f %.2f [°], %d)",
                        cell.getRange(), cell.getAngleDegrees(), cell.getVotes())));

        wheelsVelocities = computeVelocities(houghCells);
        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    private WheelsVelocities computeVelocities(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells.stream().max(HoughCell::compareTo).orElse(null);
        if (bestLine == null || isObstacleToClose) {
            String stopReason = bestLine == null ? "no walls detected" : "obstacle is too close";
            log.info(stopReason);
            return ZERO_VELOCITY;
        }

        double angle = bestLine.getAngleDegrees();
        double range = bestLine.getRange();

        log.info(String.format("best line: %.2f[°], %.2f[m]", angle, range));

        //        double rightWheeslVelocity = angle <
        double angleDelta = Math.signum(angle) * (-90.0 - angle);
        double leftSpeed =  2.0 - (angleDelta / 90.0) * 2.0;
        double rightSpeed =  2.0 + (angleDelta / 90.0) * 2.0;

        return new WheelsVelocities(leftSpeed, rightSpeed, leftSpeed, rightSpeed);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleToClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);
    }

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
