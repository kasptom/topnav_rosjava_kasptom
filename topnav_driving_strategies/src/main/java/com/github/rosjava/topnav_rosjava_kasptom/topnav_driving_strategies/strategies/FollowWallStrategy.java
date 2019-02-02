package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.HoughUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.FollowWall.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.TOO_CLOSE_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class FollowWallStrategy implements IDrivingStrategy {

    private final Log log;
    private HashMap<String, GuidelineParam> guidelineParamsMap = new HashMap<>();

    private WheelsVelocitiesChangeListener wheelsListener;

    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    private int lineDetectionThreshold = 8;
    private WheelsVelocities BASE_ROBOT_VELOCITY = new WheelsVelocities(4.0, 4.0, 4.0, 4.0);

    private boolean isObstacleToClose;
    private HeadRotationChangeListener headListener;
    private static final double RIGHT_WALL_ANGLE = -90;
    private static final double LEFT_WALL_ANGLE = 90;
    private double chosenWallAngle = LEFT_WALL_ANGLE;

    private static final double TARGET_WALL_RANGE = 0.5;

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
        return velocityCalculator.calculateRotationSpeed(
                bestLine.getAngleDegreesLidarRealm(),
                bestLine.getRange(),
                System.nanoTime(),
                chosenWallAngle,
                TARGET_WALL_RANGE);
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
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
        if (!guidelineParamsMap.containsKey(KEY_TRACKED_WALL_ALIGNMENT)) {
            log.info(String.format("Tracking wall at angle: %.2f", chosenWallAngle));
            return;
        }

        if (guidelineParamsMap.get(KEY_TRACKED_WALL_ALIGNMENT).getValue().equalsIgnoreCase(VALUE_TRACKED_WALL_LEFT)) {
            chosenWallAngle = LEFT_WALL_ANGLE;
        } else if (guidelineParamsMap.get(KEY_TRACKED_WALL_ALIGNMENT).getValue().equalsIgnoreCase(VALUE_TRACKED_WALL_RIGHT)) {
            chosenWallAngle = RIGHT_WALL_ANGLE;
        }

        log.info(String.format("Tracking wall at angle: %.2f", chosenWallAngle));
    }
}
