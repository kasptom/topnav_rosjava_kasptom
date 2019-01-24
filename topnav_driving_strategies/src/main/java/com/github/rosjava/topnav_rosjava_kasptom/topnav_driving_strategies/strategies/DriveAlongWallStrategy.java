package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.HoughUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class DriveAlongWallStrategy implements IDrivingStrategy {

    private final Log log;

    private WheelsVelocitiesChangeListener listener;

    private int lineDetectionThreshold = 8;

    private boolean isObstacleToClose;
    private HeadRotationChangeListener headListener;

    public DriveAlongWallStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void startStrategy() {
        headListener.onRotationChanged(RelativeDirection.AT_LEFT);
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

        List<HoughCell> filteredHoughCells = houghCells.stream()
                .filter(cell -> cell.getVotes() >= this.lineDetectionThreshold)
                .collect(Collectors.toList());

        WheelsVelocities wheelsVelocities = computeVelocities(filteredHoughCells);
        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    private WheelsVelocities computeVelocities(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells.stream()
                .min(Comparator.comparingDouble(HoughCell::getRange)).orElse(null);

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
        double leftSpeed = Math.abs(targetAngle - angle) <= 180
                ? (BASE_VELOCITY + MAX_VELOCITY_DELTA) / 360.0 * (angle - targetAngle) + BASE_VELOCITY
                : -(BASE_VELOCITY + MAX_VELOCITY_DELTA) / 360.0 * (angle - targetAngle + Math.signum(targetAngle - 180))
                + BASE_VELOCITY;

        double rightSpeed = Math.abs(targetAngle - angle) <= 180
                ? -(BASE_VELOCITY + MAX_VELOCITY_DELTA) / 360.0 * (angle - targetAngle) + BASE_VELOCITY
                : (BASE_VELOCITY + MAX_VELOCITY_DELTA) / 360.0 * (angle - targetAngle + Math.signum(targetAngle - 180))
                + BASE_VELOCITY;

        return new WheelsVelocities(leftSpeed, rightSpeed, leftSpeed, rightSpeed);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleToClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
        headListener = listener;
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {

    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
    }
}
