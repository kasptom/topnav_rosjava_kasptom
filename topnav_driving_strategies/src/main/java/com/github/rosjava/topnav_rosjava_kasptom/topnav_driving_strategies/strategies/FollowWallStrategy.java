package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions.IReactionStartListener;
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
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.REACTIVE_DRIVING_STRATEGY_MOVE_BACK;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.*;

public class FollowWallStrategy implements IDrivingStrategy {

    private final IReactionStartListener reactionStartListener;
    private final Log log;
    private HashMap<String, GuidelineParam> guidelineParamsMap = new HashMap<>();

    private WheelsVelocitiesChangeListener wheelsListener;
    private HeadRotationChangeRequestListener headListener;

    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createPdVelocityCalculator(0.5, 1.0, 1.0, 1.0);
    private int lineDetectionThreshold = 8;

    private boolean isObstacleTooClose;

    private static final double RIGHT_WALL_ANGLE = -90;
    private static final double LEFT_WALL_ANGLE = 90;
    private double chosenWallAngle = LEFT_WALL_ANGLE;

    public FollowWallStrategy(IReactionStartListener reactionStartListener, Log log) {
        this.reactionStartListener = reactionStartListener;
        this.log = log;
    }

    @Override
    public void startStrategy() {
        RelativeDirection initialRelativeDirection = getInitialRelativeDirection(guidelineParamsMap);
        headListener.onRotationChangeRequest(initialRelativeDirection);
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        updatePdCoefficients(configMsg.getPropCoeffAngle(),
                configMsg.getDerivCoeffAngle(),
                configMsg.getPropCoeffDist(),
                configMsg.getDerivCoeffDist());

        lineDetectionThreshold = configMsg.getLineDetectionThreshold();
        log.info(String.format("Line detection threshold: %d", lineDetectionThreshold));
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        List<HoughCell> filteredHoughCells = HoughUtils.toFilteredList(houghAcc, lineDetectionThreshold);

        if (isObstacleTooClose) {
            return;
        }

        WheelsVelocities wheelsVelocities = computeVelocities(filteredHoughCells);
        wheelsListener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleTooClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);

        if (isObstacleTooClose) {
            switchToMoveBackReaction();
        }
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
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
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

        headListener.onRotationChangeRequest(chosenWallAngle == LEFT_WALL_ANGLE
                ? RelativeDirection.AT_LEFT
                : RelativeDirection.AT_RIGHT);
    }

    private void switchToMoveBackReaction() {
        log.info("switching to move back reaction");
        wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
        reactionStartListener.onReactionStart(REACTIVE_DRIVING_STRATEGY_MOVE_BACK);
    }

    private RelativeDirection getInitialRelativeDirection(HashMap<String, GuidelineParam> guidelineParamsMap) {
        String alignment = null;
        if (guidelineParamsMap.containsKey(KEY_TRACKED_WALL_ALIGNMENT)) {
            alignment = guidelineParamsMap.get(KEY_TRACKED_WALL_ALIGNMENT).getValue();
        }
        return alignment != null && alignment.equalsIgnoreCase(VALUE_TRACKED_WALL_RIGHT)
                ? RelativeDirection.AT_RIGHT
                : RelativeDirection.AT_LEFT;
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

    private void updatePdCoefficients(double propCoeffAngle, double derivCoeffAngle, double propCoeffDist, double derivCoeffDist) {
        log.info(String.format("Changing coefficients values K_p_ang = %.2f, K_d_ang = %.2f, K_p_dst = %.2f, K_d_dst = %.2f",
                propCoeffAngle, derivCoeffAngle, propCoeffDist, derivCoeffDist));
        velocityCalculator.updateCoefficients(propCoeffAngle, derivCoeffAngle, propCoeffDist, derivCoeffDist);
    }

    private WheelsVelocities computeRotationComponent(HoughCell bestLine) {
        return velocityCalculator.calculateRotationSpeed(
                bestLine.getAngleDegreesLidarDomain(),
                bestLine.getRange(),
                System.nanoTime(),
                chosenWallAngle,
                TARGET_WALL_RANGE);
    }
}
