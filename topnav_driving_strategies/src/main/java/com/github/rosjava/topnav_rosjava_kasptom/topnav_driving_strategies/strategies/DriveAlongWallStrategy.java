package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils.HoughUtils;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.HoughCell;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils.MathUtils;
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

    private static final double PARALLEL_TO_LEFT_WALL_ANGLE = 270;
    private static final double AHEAD_THE_WALL = 180;

    private WheelsVelocitiesChangeListener listener;

    private int lineDetectionThreshold = 5;

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
        List<HoughCell> houghCells = HoughUtils.toList(houghAcc);

        houghCells
                .stream()
                .filter(cell -> cell.getVotes() >= this.lineDetectionThreshold)
                .forEach(cell -> log.info(String.format(
                        "detected line: (dst, ang, vts) = (%.2f %.2f [°], %d)",
                        cell.getRange(), cell.getAngleDegrees(), cell.getVotes())));

        List<HoughCell> filteredHoughCells = houghCells.stream()
                .filter(cell -> cell.getVotes() >= this.lineDetectionThreshold)
                .collect(Collectors.toList());

        WheelsVelocities wheelsVelocities = computeVelocities(filteredHoughCells);
        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    private WheelsVelocities computeVelocities(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells.stream().max(HoughCell::compareTo).orElse(null);
        if (isObstacleToClose) {
            log.info("obstacle is too close");
            return ZERO_VELOCITY;
        }

        if (bestLine == null) {
            log.info("no walls detected... driving straight ahead");
            return new WheelsVelocities(2.0, 2.0, 2.0, 2.0);
        }

        double angle = (bestLine.getAngleDegrees() + 90) % 360;
        double range = bestLine.getRange();

        log.info(String.format("best line: %.2f[°], %.2f[m]", angle, range));

        WheelsVelocities velocities;
        if (isInCloseRange(range)) {
            log.info("parallel to the wall");
            velocities = keepTargetAngle(angle, PARALLEL_TO_LEFT_WALL_ANGLE);
        } else {
            log.info("closing to the wall");
            velocities = keepTargetAngle(angle, AHEAD_THE_WALL);
        }

        return velocities;
    }

    private boolean isInCloseRange(double range) {
        return range > TOO_CLOSE_RANGE && range <= 5 * TOO_CLOSE_RANGE;
    }

    public WheelsVelocities keepTargetAngle(double angle, double targetAngle) {
        double diffMod = MathUtils.modulo(-angle - targetAngle, 360) - 180;

        double leftSpeed = 2.0 - (diffMod / 180.0) * 2.0;
        double rightSpeed = 2.0 + (diffMod / 180.0) * 2.0;

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
}
