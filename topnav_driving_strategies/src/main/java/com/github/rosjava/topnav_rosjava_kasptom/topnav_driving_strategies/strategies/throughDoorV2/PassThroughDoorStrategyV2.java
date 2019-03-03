package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoorV2;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import topnav_msgs.*;

import java.util.HashMap;
import java.util.List;

public class PassThroughDoorStrategyV2 implements IDrivingStrategy, ArUcoMessageListener {
    private final IArUcoHeadTracker arucoTracker;
    private HashMap<String, GuidelineParam> nameToParameter;
    private WheelsVelocitiesChangeListener wheelsListener;
    private PdVelocityCalculator velocityCalculator;

    public PassThroughDoorStrategyV2(IArUcoHeadTracker arUcoTracker) {
        this.arucoTracker = arUcoTracker;
        nameToParameter = new HashMap<>();
        velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    }

    @Override
    public void startStrategy() {
        arucoTracker.setTrackedMarkers(ArucoMarkerUtils.asOrderedDoorMarkerIds(nameToParameter));
        arucoTracker.start();
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {

    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {

    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.wheelsListener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {

    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {

    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, nameToParameter);
    }

    @Override
    public void handleArUcoMessage(MarkersMsg message) {

    }
}
