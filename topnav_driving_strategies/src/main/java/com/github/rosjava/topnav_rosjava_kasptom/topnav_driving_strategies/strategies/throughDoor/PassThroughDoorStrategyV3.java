package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.markerRelativePositioning.PositionAccordingToMarkerStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.DriveThroughAndLookForBackMarkers;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.*;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import std_msgs.UInt64;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PassThroughDoorStrategyV3 implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener, IClockMessageHandler, StrategyFinishedListener {

    private final IArUcoHeadTracker arUcoTracker;
    private final Log log;
    private final HashMap<String, GuidelineParam> guidelineParamsMap;

    private CompoundStrategyStage currentStage;
    private StrategyFinishedListener strategyFinishedListener;

    private PositionAccordingToMarkerStrategy positionStrategy;
    private DriveThroughAndLookForBackMarkers throughDoorStrategy;

    private WheelsVelocitiesChangeListener wheelsListener;
    private HeadRotationChangeRequestListener headRotationListener;

    public PassThroughDoorStrategyV3(IArUcoHeadTracker arUcoTracker, Log log) {
        this.arUcoTracker = arUcoTracker;
        this.log = log;

        guidelineParamsMap = new HashMap<>();
        currentStage = CompoundStrategyStage.INITIAL;
    }


    @Override
    public void startStrategy() {
        positionStrategy = new PositionAccordingToMarkerStrategy(arUcoTracker, log);
        throughDoorStrategy = new DriveThroughAndLookForBackMarkers(wheelsListener, headRotationListener, null, this, guidelineParamsMap, log);

        List<GuidelineParam> guidelineParams = rewriteGuidelinesForInnerStrategy();
        List<String> rewrittenParams = GuidelineUtils.convertToStrings(guidelineParams);
        positionStrategy.setGuidelineParameters(rewrittenParams);

        positionStrategy.setHeadRotationChangeListener(headRotationListener);
        positionStrategy.setWheelsVelocitiesListener(wheelsListener);
        positionStrategy.setStrategyFinishedListener(this);

        positionStrategy.startStrategy();
        currentStage = CompoundStrategyStage.ALIGN_BETWEEN_DOOR;
    }

    @Override
    public void handleClockMessage(UInt64 clockMsg) {
        if (currentStage != CompoundStrategyStage.ALIGN_BETWEEN_DOOR) {
            return;
        }

        positionStrategy.handleClockMessage(clockMsg);
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        if (currentStage != CompoundStrategyStage.DRIVE_THROUGH_DOOR) {
            return;
        }

        throughDoorStrategy.handleAngleRangeMessage(angleRangesMsg);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        if (currentStage != CompoundStrategyStage.DRIVE_THROUGH_DOOR) {
            return;
        }

        throughDoorStrategy.handleDetectionMessage(feedbackMsg);
    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {
        if (currentStage != CompoundStrategyStage.ALIGN_BETWEEN_DOOR) {
            return;
        }
        positionStrategy.handleHeadDirectionChange(relativeDirectionMsg);
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsListener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
        headRotationListener = listener;
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
        strategyFinishedListener = listener;
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        if (currentStage != CompoundStrategyStage.ALIGN_BETWEEN_DOOR) {
            return;
        }
        positionStrategy.onTrackedMarkerUpdate(detection, headRotation);
    }

    @Override
    public void onStrategyFinished(boolean isSuccess) {
        if (!isSuccess) {
            strategyFinishedListener.onStrategyFinished(false);
            return;
        }

        if (currentStage == CompoundStrategyStage.ALIGN_BETWEEN_DOOR) {
            headRotationListener.onRotationChangeRequest(RelativeDirection.BEHIND);
            throughDoorStrategy.startStrategy();
            currentStage = CompoundStrategyStage.DRIVE_THROUGH_DOOR;
        } else if (currentStage == CompoundStrategyStage.DRIVE_THROUGH_DOOR) {
            strategyFinishedListener.onStrategyFinished(true);
        }
    }

    private List<GuidelineParam> rewriteGuidelinesForInnerStrategy() {
        String fullRobotRotationTimeMs = guidelineParamsMap.get(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS).getValue();

        String frontLeftMarker = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID).getValue();
        String frontRightMarker = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue();

        GuidelineParam leftApproachedMarkerParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_1, frontLeftMarker, "String");
        GuidelineParam leftRelativeDirectionParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_1, RelativeDirection.AHEAD.name(), "String");
        GuidelineParam leftRelativeDistanceParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_1, RelativeAlignment.LEFT.name(), "String");

        GuidelineParam rightApproachedMarkerParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_2, frontRightMarker, "String");
        GuidelineParam rightRelativeDirectionParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_2, RelativeDirection.AHEAD.name(), "String");
        GuidelineParam rightRelativeDistanceParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_2, RelativeAlignment.RIGHT.name(), "String");

        GuidelineParam fullRobotRotationMsParam = new GuidelineParam(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS, fullRobotRotationTimeMs, "Long");

        return Arrays.asList(
                fullRobotRotationMsParam,
                leftApproachedMarkerParam,
                leftRelativeDirectionParam,
                leftRelativeDistanceParam,
                rightApproachedMarkerParam,
                rightRelativeDirectionParam,
                rightRelativeDistanceParam);
    }
}
