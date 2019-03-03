package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.HashMap;
import java.util.List;

public abstract class BasePassThroughDoorStrategy implements IDrivingStrategy {
    protected final Log log;
    boolean isHeadRotationInProgress;
    HashMap<String, GuidelineParam> guidelineParamsMap;
    HashMap<ThroughDoorStage, IDrivingStrategy> substrategies;

    ThroughDoorStage currentStage;
    HeadRotationChangeListener headListener;
    WheelsVelocitiesChangeListener wheelsListener;
    StrategyFinishedListener strategyFinishedListener;


    BasePassThroughDoorStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        BlockedMessageHandler.handleIfNotBlocked(
                configMsg,
                msg -> this.substrategies.get(currentStage).handleConfigMessage(msg),
                isHeadRotationInProgress);
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        BlockedMessageHandler.handleIfNotBlocked(
                houghAcc,
                msg -> this.substrategies.get(currentStage).handleHoughAccMessage(msg),
                isHeadRotationInProgress);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        BlockedMessageHandler.handleIfNotBlocked(
                angleRangesMsg,
                msg -> this.substrategies.get(currentStage).handleAngleRangeMessage(msg),
                isHeadRotationInProgress);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        BlockedMessageHandler.handleIfNotBlocked(
                feedbackMsg,
                msg -> this.substrategies.get(currentStage).handleDetectionMessage(msg),
                isHeadRotationInProgress);
    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {
        String relativeDirection = relativeDirectionMsg.getData();
        log.info(String.format("Head direction changed to %s", relativeDirection));
        isHeadRotationInProgress = false;
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsListener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
        headListener = listener;
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
        strategyFinishedListener = listener;
    }

    @Override
    public void setGuidelineParameters(List<String> guidelineParameters) {
        GuidelineUtils.reloadParameters(guidelineParameters, guidelineParamsMap);
    }
}
