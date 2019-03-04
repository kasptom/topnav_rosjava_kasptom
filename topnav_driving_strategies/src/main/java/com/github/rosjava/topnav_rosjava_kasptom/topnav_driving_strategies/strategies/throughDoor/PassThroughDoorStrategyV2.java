package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.RotateTheChassisSideTowardsDoorStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;

import java.util.HashMap;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage.DETECT_MARKER;
import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage.TRACK_MARKER;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.AT_LEFT;

public class PassThroughDoorStrategyV2 extends BasePassThroughDoorStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener {
    private final IArUcoHeadTracker arUcoTracker;

    public PassThroughDoorStrategyV2(IArUcoHeadTracker arUcoTracker, Log log) {
        super(log);
        this.arUcoTracker = arUcoTracker;
    }

    @Override
    void initializeSubStrategies() {
        this.subStrategies = new HashMap<>();
        this.subStrategies.put(DETECT_MARKER, new RotateTheChassisSideTowardsDoorStrategy(wheelsListener, headListener, this, strategyFinishedListener, log, guidelineParamsMap));
        this.subStrategies.put(TRACK_MARKER, new TrackMarkerStrategy(wheelsListener, headListener, this, strategyFinishedListener, guidelineParamsMap, log));
    }

    @Override
    ThroughDoorStage[] getSubStrategiesExecutionOrder() {
        return new ThroughDoorStage[] {DETECT_MARKER, TRACK_MARKER};
    }

    @Override
    public void startStrategy() {
        initializeSubStrategies();
        arUcoTracker.setTrackedMarkers(GuidelineUtils.asOrderedDoorMarkerIds(guidelineParamsMap));
        switchToInitialStage(AT_LEFT);
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        if (!getCurrentStage().equals(TRACK_MARKER) || isHeadRotationInProgress) {
            return;
        }

        ((TrackMarkerStrategy) this.subStrategies.get(getCurrentStage())).onTrackedMarkerUpdate(detection, headRotation);
    }

    @Override
    public void onStageFinished(ThroughDoorStage finishedStage, RelativeDirection direction) {
        super.onStageFinished(finishedStage, direction);
        if (finishedStage.equals(DETECT_MARKER)) {
            arUcoTracker.start(AT_LEFT.getRotationDegrees());
        }
    }
}
