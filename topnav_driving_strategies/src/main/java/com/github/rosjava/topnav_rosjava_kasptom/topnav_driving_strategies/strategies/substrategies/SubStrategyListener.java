package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

public interface SubStrategyListener {
    void onStageFinished(ThroughDoorStage finishedStage, RelativeDirection direction);

    void headRotationInProgress(boolean isInProgress);
}
