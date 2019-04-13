package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.*;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;

import java.util.HashMap;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.UNDEFINED;

public class PassThroughDoorStrategyV2 extends BaseCompoundStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener {
    private final IArUcoHeadTracker arUcoTracker;

    public PassThroughDoorStrategyV2(IArUcoHeadTracker arUcoTracker, Log log) {
        super(log);
        this.arUcoTracker = arUcoTracker;
    }

    @Override
    public void initializeSubStrategies() {
        subStrategies = new HashMap<>();
        subStrategies.put(DETECT_MARKER, new DetectMarkerSubStrategy(arUcoTracker, wheelsListener, headListener, this, strategyFinishedListener, log, guidelineParamsMap));
        subStrategies.put(TRACK_MARKER, new TrackMarkerSubStrategy(wheelsListener, headListener, this, strategyFinishedListener, guidelineParamsMap, log));
        subStrategies.put(DRIVE_THROUGH_DOOR, new DriveThroughAndLookForBackMarkers(wheelsListener, headListener, this, strategyFinishedListener, guidelineParamsMap, log));
    }

    @Override
    public CompoundStrategyStage[] getSubStrategiesExecutionOrder() {
        return new CompoundStrategyStage[]{DETECT_MARKER, TRACK_MARKER, DRIVE_THROUGH_DOOR};
    }

    @Override
    public void startStrategy() {
        initializeSubStrategies();
        arUcoTracker.setTrackedMarkers(GuidelineUtils.asOrderedDoorMarkerIds(guidelineParamsMap));
        switchToInitialStage(UNDEFINED);
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        if (isHeadRotationInProgress) {
            return;
        }

        if (getCurrentStage().equals(DETECT_MARKER)) {
            ((DetectMarkerSubStrategy) this.subStrategies.get(getCurrentStage())).onTrackedMarkerUpdate(detection, headRotation);
        }

        if (getCurrentStage().equals(TRACK_MARKER)) {
            ((TrackMarkerSubStrategy) this.subStrategies.get(getCurrentStage())).onTrackedMarkerUpdate(detection, headRotation);
        }
    }

    @Override
    public void onStageFinished(CompoundStrategyStage finishedStage, RelativeDirection direction) {
        super.onStageFinished(finishedStage, direction);
        if (direction != UNDEFINED) {
            this.arUcoTracker.stop();
        }
    }
}
