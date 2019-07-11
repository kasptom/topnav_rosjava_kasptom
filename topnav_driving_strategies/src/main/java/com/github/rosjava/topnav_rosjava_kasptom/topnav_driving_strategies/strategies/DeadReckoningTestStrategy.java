package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.DeadReckoningDrive;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IDeadReckoningManeuverListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import std_msgs.UInt64;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.HashMap;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;

public class DeadReckoningTestStrategy implements IDrivingStrategy, IDeadReckoningManeuverListener, WheelsVelocitiesChangeListener, IClockMessageHandler {

    private DeadReckoningDrive deadReckoningDrive;
    private StrategyFinishedListener strategyFinishedListener;
    private HashMap<String, GuidelineParam> guidelineParamsMap;
    private WheelsVelocitiesChangeListener wheelsListener;

    public DeadReckoningTestStrategy() {
        guidelineParamsMap = new HashMap<>();
    }

    @Override
    public void startStrategy() {
        String maneuverName = guidelineParamsMap.get(KEY_MANEUVER_NAME).getValue();
        double angleDegrees = Double.parseDouble(guidelineParamsMap.get(KEY_MANEUVER_ANGLE_DEGREES).getValue());
        double distanceMeters = Double.parseDouble(guidelineParamsMap.get(KEY_MANEUVER_DISTANCE_METERS).getValue());
        long fullRotationTimeMilliseconds = Long.parseLong(guidelineParamsMap.get(KEY_MANEUVER_WHEEL_FULL_ROTATION_MS).getValue());

        deadReckoningDrive = new DeadReckoningDrive(CASE_WIDTH + WHEEL_WIDTH, WHEEL_DIAMETER, fullRotationTimeMilliseconds);
        deadReckoningDrive.setManeuverFinishListener(this);
        deadReckoningDrive.setWheelsVelocitiesListener(this);

        deadReckoningDrive.startManeuver(maneuverName, angleDegrees, distanceMeters);
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        deadReckoningDrive.onAngleRangeMessage(angleRangesMsg);
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

    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
        strategyFinishedListener = listener;
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
    }

    @Override
    public void onManeuverFinished(boolean isWithoutObstacles) {
        // TODO reaction and try again
        strategyFinishedListener.onStrategyFinished(isWithoutObstacles);
    }

    @Override
    public void onWheelsVelocitiesChanged(WheelsVelocities velocities) {
        wheelsListener.onWheelsVelocitiesChanged(velocities);
    }

    @Override
    public void handleClockMessage(UInt64 clockMsg) {
        deadReckoningDrive.onClockMessage(clockMsg);
    }
}
