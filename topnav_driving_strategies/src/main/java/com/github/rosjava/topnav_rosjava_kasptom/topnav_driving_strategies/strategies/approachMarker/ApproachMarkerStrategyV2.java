package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.approachMarker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.markerRelativePositioning.PositionAccordingToMarkerStrategy;
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

import static com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam.EMPTY_PARAM_VALUE;

public class ApproachMarkerStrategyV2 implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener, IClockMessageHandler {

    private final PositionAccordingToMarkerStrategy positionStrategy;
    private final HashMap<String, GuidelineParam> guidelineParamsMap;

    private RelativeDirection direction;
    private RelativeAlignment alignment;

    public ApproachMarkerStrategyV2(IArUcoHeadTracker arUcoTracker, Log log) {
        guidelineParamsMap = new HashMap<>();
        positionStrategy = new PositionAccordingToMarkerStrategy(arUcoTracker, log);
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);

        alignment = getRelativeAlignment(guidelineParamsMap);
        direction = getRelativeDirection(guidelineParamsMap);

        List<GuidelineParam> guidelineParams = rewriteGuidelinesForInnerStrategy();
        List<String> rewrittenParams = GuidelineUtils.convertToStrings(guidelineParams);
        positionStrategy.setGuidelineParameters(rewrittenParams);
    }

    @Override
    public void handleClockMessage(UInt64 clockMsg) {
        positionStrategy.handleClockMessage(clockMsg);
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        positionStrategy.setWheelsVelocitiesListener(listener);
    }

    @Override
    public void startStrategy() {
        positionStrategy.startStrategy();
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
        positionStrategy.setHeadRotationChangeListener(listener);
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
        positionStrategy.setStrategyFinishedListener(listener);
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        positionStrategy.handleConfigMessage(configMsg);
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        positionStrategy.handleHoughAccMessage(houghAcc);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        positionStrategy.handleAngleRangeMessage(angleRangesMsg);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        positionStrategy.handleDetectionMessage(feedbackMsg);
    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {
        positionStrategy.handleHeadDirectionChange(relativeDirectionMsg);
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        positionStrategy.onTrackedMarkerUpdate(detection, headRotation);
    }

    private RelativeAlignment getRelativeAlignment(HashMap<String, GuidelineParam> guidelineParamsMap) {
        String relativeAlignment = guidelineParamsMap.getOrDefault(DrivingStrategy.ApproachMarker.KEY_APPROACHED_ALIGNMENT, GuidelineParam.getEmptyParam()).getValue();
        if (relativeAlignment.equals(EMPTY_PARAM_VALUE)) {
            return RelativeAlignment.CENTER;
        }

        return RelativeAlignment.valueOf(relativeAlignment.toUpperCase());
    }

    private RelativeDirection getRelativeDirection(HashMap<String, GuidelineParam> guidelineParamsMap) {
        String relativeDirection = guidelineParamsMap.getOrDefault(DrivingStrategy.ApproachMarker.KEY_APPROACHED_DIRECTION, GuidelineParam.getEmptyParam()).getValue();
        if (relativeDirection.equals(EMPTY_PARAM_VALUE)) {
            return RelativeDirection.AHEAD;
        }

        return RelativeDirection.valueOf(relativeDirection.toUpperCase());
    }

    private List<GuidelineParam> rewriteGuidelinesForInnerStrategy() {
        String approachedMarkerId = guidelineParamsMap.get(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID).getValue();
        String approachedMarkerId2 = guidelineParamsMap.getOrDefault(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID_2, GuidelineParam.getEmptyParam()).getValue();
        String fullRobotRotationTimeMs = guidelineParamsMap.get(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS).getValue();

        GuidelineParam approachedMarkerParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_1, approachedMarkerId, "String");
        GuidelineParam approachedMarkerParam2 = !approachedMarkerId2.equals(EMPTY_PARAM_VALUE)
                ? new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_2, approachedMarkerId2, "String")
                : GuidelineParam.getEmptyParam();

        GuidelineParam relativeDirectionParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_1, direction.name(), "String");
        GuidelineParam relativeDistanceParam = new GuidelineParam(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_1, alignment.name(), "String");

        GuidelineParam fullRobotRotationMsParam = new GuidelineParam(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS, fullRobotRotationTimeMs, "Long");

        List<GuidelineParam> params = Arrays.asList(approachedMarkerParam, approachedMarkerParam2, relativeDirectionParam, relativeDistanceParam, fullRobotRotationMsParam);
        params.remove(GuidelineParam.getEmptyParam());
        return params;
    }
}
