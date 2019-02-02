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
import std_msgs.String;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.TOO_CLOSE_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class FollowWallStrategy implements IDrivingStrategy {

    private final Log log;

    private WheelsVelocitiesChangeListener wheelsListener;

    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    private int lineDetectionThreshold = 8;
    private WheelsVelocities BASE_ROBOT_VELOCITY = new WheelsVelocities(4.0, 4.0, 4.0, 4.0);

    private boolean isObstacleToClose;
    private HeadRotationChangeListener headListener;

    public FollowWallStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void startStrategy() {
        // TODO camera direction according to the angle where the wall is detected
        headListener.onRotationChanged(RelativeDirection.AT_LEFT);
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        if (isObstacleToClose) {
            log.info("obstacle is too close");
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            return;
        }

        List<HoughCell> filteredHoughCells = HoughUtils.toFilteredList(houghAcc, lineDetectionThreshold);
        WheelsVelocities wheelsVelocities = computeVelocities(filteredHoughCells);
        wheelsListener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    private WheelsVelocities computeVelocities(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells.stream()
                .min(Comparator.comparingDouble(HoughCell::getRange))
                .orElse(null);

        if (bestLine == null) {
            return BASE_ROBOT_VELOCITY;
        }

        WheelsVelocities rotationVelocityComponent = computeRotationComponent(bestLine);

        return WheelsVelocities.addVelocities(BASE_ROBOT_VELOCITY, rotationVelocityComponent);
    }

    private WheelsVelocities computeRotationComponent(HoughCell bestLine) {
        return velocityCalculator.calculateRotationSpeed(bestLine.getAngleDegreesLidarRealm(), bestLine.getRange(), System.nanoTime(), -90, 0.5);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleToClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void handleHeadDirectionChange(String relativeDirectionMsg) {

    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsListener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
        headListener = listener;
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {

    }

    @Override
    public void setGuidelineParameters(List<java.lang.String> parameters) {

    }
}
