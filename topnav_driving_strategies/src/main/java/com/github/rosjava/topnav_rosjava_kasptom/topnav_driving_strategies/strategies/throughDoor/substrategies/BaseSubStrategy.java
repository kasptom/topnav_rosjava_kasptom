package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import topnav_msgs.TopNavConfigMsg;

import java.util.HashMap;
import java.util.List;

public abstract class BaseSubStrategy implements IDrivingStrategy {
    final HeadRotationChangeRequestListener headListener;
    final StrategyFinishedListener finishListener;
    protected final WheelsVelocitiesChangeListener wheelsListener;
    protected final SubStrategyListener subStrategyListener;
    protected final HashMap<String, GuidelineParam> guidelineParamsMap;


    public BaseSubStrategy(WheelsVelocitiesChangeListener wheelsListener, HeadRotationChangeRequestListener headListener, SubStrategyListener subStrategyListener, StrategyFinishedListener finishListener, HashMap<String, GuidelineParam> guidelineParamsMap) {
        this.wheelsListener = wheelsListener;
        this.headListener = headListener;
        this.subStrategyListener = subStrategyListener;
        this.finishListener = finishListener;
        this.guidelineParamsMap = guidelineParamsMap;
    }

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
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
    }
}
