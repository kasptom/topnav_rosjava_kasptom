package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;

import java.util.HashMap;

public class PassThroughDoorStrategyV2 extends BasePassThroughDoorStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener {
    private final IArUcoHeadTracker arUcoTracker;

    private HashMap<String, GuidelineParam> nameToParameter;
    private PdVelocityCalculator velocityCalculator;

    public PassThroughDoorStrategyV2(IArUcoHeadTracker arUcoTracker, Log log) {
        super(log);
        this.arUcoTracker = arUcoTracker;
        nameToParameter = new HashMap<>();
        velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    }

    @Override
    public void startStrategy() {
        arUcoTracker.setTrackedMarkers(GuidelineUtils.asOrderedDoorMarkerIds(nameToParameter));

        RelativeDirection relativeDirection = rotateHeadTowardsDoorMarkers();
        if (relativeDirection.equals(RelativeDirection.UNDEFINED)) {
            log.info("could not find door markers around");
            strategyFinishedListener.onStrategyFinished(false);
        }
    }

    private RelativeDirection rotateHeadTowardsDoorMarkers() {
        return RelativeDirection.UNDEFINED;
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        RelativeDirection targetDirection = RelativeDirection.UNDEFINED;

        if (isDoorMarker(detection, DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID)) {
            targetDirection = RelativeDirection.AT_LEFT;
        } else if (isDoorMarker(detection, DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID)) {
            targetDirection = RelativeDirection.AT_RIGHT;
        } else {
            log.info("No front door marker was found");
            strategyFinishedListener.onStrategyFinished(false);
            return;
        }
        // TODO pdvelocity i sledzenie


        return;
    }

    private boolean isDoorMarker(MarkerDetection detection, String doorMarkerParamKey) {
        return detection.getId().equalsIgnoreCase(nameToParameter.get(doorMarkerParamKey).getValue());
    }
}
