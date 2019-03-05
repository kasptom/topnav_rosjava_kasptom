package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.SubStrategyListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class BasePassThroughDoorStrategy implements IDrivingStrategy, SubStrategyListener {
    protected final Log log;
    boolean isHeadRotationInProgress;
    HashMap<String, GuidelineParam> guidelineParamsMap;
    HashMap<ThroughDoorStage, IDrivingStrategy> subStrategies;
    private List<ThroughDoorStage> subStrategiesOrdered;

    private ThroughDoorStage currentStage;
    HeadRotationChangeRequestListener headListener;
    WheelsVelocitiesChangeListener wheelsListener;
    StrategyFinishedListener strategyFinishedListener;


    BasePassThroughDoorStrategy(Log log) {
        this.log = log;
        this.guidelineParamsMap = new HashMap<>();
        initializeSubStrategies();
        subStrategiesOrdered = Arrays.asList(getSubStrategiesExecutionOrder());
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        IDrivingStrategy subStrategy = this.subStrategies.get(currentStage);
        BlockedMessageHandler.handleIfNotBlocked(
                configMsg,
                subStrategy::handleConfigMessage,
                isHeadRotationInProgress);
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        IDrivingStrategy subStrategy = this.subStrategies.get(currentStage);
        BlockedMessageHandler.handleIfNotBlocked(
                houghAcc,
                subStrategy::handleHoughAccMessage,
                isHeadRotationInProgress);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        IDrivingStrategy subStrategy = this.subStrategies.get(currentStage);
        BlockedMessageHandler.handleIfNotBlocked(
                angleRangesMsg,
                subStrategy::handleAngleRangeMessage,
                isHeadRotationInProgress);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        IDrivingStrategy subStrategy = this.subStrategies.get(currentStage);
        BlockedMessageHandler.handleIfNotBlocked(
                feedbackMsg,
                subStrategy::handleDetectionMessage,
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
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
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

    @Override
    public void onStageFinished(ThroughDoorStage finishedStage, RelativeDirection direction) {
        switchToNextStageFrom(finishedStage, direction);
    }

    @Override
    public void headRotationInProgress(boolean isInProgress) {
        isHeadRotationInProgress = isInProgress;
    }

    abstract void initializeSubStrategies();

    abstract ThroughDoorStage[] getSubStrategiesExecutionOrder();

    void switchToInitialStage(@SuppressWarnings("SameParameterValue") RelativeDirection direction) {
        currentStage = subStrategiesOrdered.get(0);
        isHeadRotationInProgress = true;
        headListener.onRotationChangeRequest(direction);
        subStrategies.get(currentStage).startStrategy();
    }

    ThroughDoorStage getCurrentStage() {
        return currentStage;
    }

    private void switchToNextStageFrom(ThroughDoorStage finishedStage, RelativeDirection direction) {
        log.info(String.format("Switching to the next stage from %s", finishedStage));

        int finishedIdx = subStrategiesOrdered.indexOf(finishedStage);
        if (finishedIdx + 1 < subStrategiesOrdered.size()) {
            currentStage = subStrategiesOrdered.get(finishedIdx + 1);
        } else {
            log.info("Last stage reached. Finishing");
            strategyFinishedListener.onStrategyFinished(true);
            return;
        }

        isHeadRotationInProgress = true;
        headListener.onRotationChangeRequest(direction);
        subStrategies.get(currentStage).startStrategy();
    }
}
