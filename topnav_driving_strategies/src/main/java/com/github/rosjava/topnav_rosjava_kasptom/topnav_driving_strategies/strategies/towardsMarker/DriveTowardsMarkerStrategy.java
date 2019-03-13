package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.towardsMarker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.BaseCompoundStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.ThroughDoorStage;
import org.apache.commons.logging.Log;

public class DriveTowardsMarkerStrategy extends BaseCompoundStrategy {

    private final IArUcoHeadTracker arucoTracker;

    public DriveTowardsMarkerStrategy(IArUcoHeadTracker arucoTracker, Log log) {
        super(log);
        this.arucoTracker = arucoTracker;
    }

    @Override
    public void initializeSubStrategies() {

    }

    @Override
    public ThroughDoorStage[] getSubStrategiesExecutionOrder() {
        return new ThroughDoorStage[0];
    }

    @Override
    public void startStrategy() {

    }
}
