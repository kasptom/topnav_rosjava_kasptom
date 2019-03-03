package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

public interface SubStrategyListener {
    void onStageChanged(ThroughDoorStage stage, RelativeDirection direction);

    void headRotationInProgress(boolean isInProgress);
}
