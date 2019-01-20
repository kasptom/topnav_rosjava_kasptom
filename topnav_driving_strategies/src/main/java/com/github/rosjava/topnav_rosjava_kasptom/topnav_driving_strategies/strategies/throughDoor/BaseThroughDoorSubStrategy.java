package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import topnav_msgs.TopNavConfigMsg;

import java.util.List;

abstract class BaseThroughDoorSubStrategy implements IDrivingStrategy {

    @Override
    public void startStrategy() {
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
    }
}
