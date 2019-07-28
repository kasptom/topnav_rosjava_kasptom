package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.HashMap;
import java.util.List;

public class AruCoTrackerTestStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener {

    private final IArUcoHeadTracker arucoTracker;
    private HashMap<String, GuidelineParam> guidelineParamsMap;

    public AruCoTrackerTestStrategy(IArUcoHeadTracker arUcoTracker) {
        this.guidelineParamsMap = new HashMap<>();
        this.arucoTracker = arUcoTracker;
    }

    @Override
    public void startStrategy() {
        arucoTracker.setTrackedMarkers(GuidelineUtils.asOrderedDoorMarkerIds(guidelineParamsMap));
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

    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {

    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {

    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        System.out.println(detection);
    }
}
