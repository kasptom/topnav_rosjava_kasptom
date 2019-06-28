package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.approachMarker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.BaseCompoundStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.DetectMarkerSubStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;

import java.util.HashMap;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.UNDEFINED;

public class ApproachMarkerStrategy extends BaseCompoundStrategy implements IArUcoHeadTracker.TrackedMarkerListener {

    private final IArUcoHeadTracker arUcoTracker;

    public ApproachMarkerStrategy(IArUcoHeadTracker arUcoTracker, Log log) {
        super(log);
        this.arUcoTracker = arUcoTracker;
    }

    @Override
    public void initializeSubStrategies() {
        subStrategies = new HashMap<>();
        subStrategies.put(DETECT_MARKER, new DetectMarkerSubStrategy(arUcoTracker, wheelsListener, headListener, this, strategyFinishedListener, log, guidelineParamsMap));
        subStrategies.put(APPROACH_MARKER, new ApproachArUcoSubStrategy(wheelsListener, headListener, this, strategyFinishedListener, guidelineParamsMap, log));
    }

    @Override
    public CompoundStrategyStage[] getSubStrategiesExecutionOrder() {
        return new CompoundStrategyStage[]{DETECT_MARKER, APPROACH_MARKER};
    }

    @Override
    public void startStrategy() {
        initializeSubStrategies();
        arUcoTracker.setTrackedMarkers(GuidelineUtils.approachedMarkerIdAsSet(guidelineParamsMap));
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
        if (getCurrentStage().equals(APPROACH_MARKER)) {
            ((ApproachArUcoSubStrategy) this.subStrategies.get(getCurrentStage())).onTrackedMarkerUpdate(detection, headRotation);
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
