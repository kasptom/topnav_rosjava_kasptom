package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorStrategy.ThroughDoorStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class PassThroughDoorStrategy implements IDrivingStrategy {
    private final Log log;

    private HeadRotationChangeListener headListener;
    private WheelsVelocitiesChangeListener wheelsListener;
    private StrategyFinishedListener strategyFinishedListener;

    private HashMap<String, GuidelineParam> guidelineParamsMap;
    private HashMap<ThroughDoorStage, IDrivingStrategy> substrategies;
    private ThroughDoorStage currentStage;

    private boolean isHeadRotationInProgress;

    public PassThroughDoorStrategy(Log log) {
        this.log = log;
        this.currentStage = DETECTED_MARKER;
        guidelineParamsMap = new HashMap<>();
        substrategies = initializeSubstrategies();
    }

    private HashMap<ThroughDoorStage, IDrivingStrategy> initializeSubstrategies() {
        this.substrategies = new HashMap<>();
        this.substrategies.put(DETECTED_MARKER, new RotateTheChassisSideTowardsDoorStrategy());
        this.substrategies.put(ROTATED_SIDE_TOWARDS_DOOR, new AlignBetweenDoorMarkersStrategy());
        this.substrategies.put(ALIGNED_WITH_DOOR, new RotateTheChassisFrontTowardsDoorStrategy());
        this.substrategies.put(ROTATED_TOWARDS_DOOR, new DriveThroughDoorStrategy());
        return substrategies;
    }

    @Override
    public void startStrategy() {
        isHeadRotationInProgress = true;
        headListener.onRotationChanged(AT_LEFT);
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

    enum ThroughDoorStage {
        DETECTED_MARKER,
        ROTATED_SIDE_TOWARDS_DOOR,
        ALIGNED_WITH_DOOR,
        ROTATED_TOWARDS_DOOR,
        BACK_MARKER_SPOTTED,
    }


    // DETECTED_MARKER
    class RotateTheChassisSideTowardsDoorStrategy extends BaseThroughDoorSubStrategy {
        List<RelativeDirection> directionsToCheck = Arrays.asList(AT_LEFT, AHEAD, AT_RIGHT, BEHIND);
        int checkedDirection = 0;

        boolean isChassisRotationInProgress;

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
            if (!isChassisRotationInProgress) {
                return;
            }
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {

        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
            String leftMarkerId = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID).getValue();
            String rightMarkerId = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue();

            List<TopologyMsg> expectedDoorMarkers = feedbackMsg.getTopologies()
                    .stream()
                    .filter(topologyMsg -> topologyMsg.getIdentity().equals(leftMarkerId)
                            || topologyMsg.getIdentity().equals(rightMarkerId))
                    .collect(Collectors.toList());

            if (isChassisRotationInProgress) {
                if (expectedDoorMarkers.size() > 0) {
                    wheelsListener.onWheelsVelocitiesChanged(WheelsVelocityConstants.ZERO_VELOCITY);
                    strategyFinishedListener.onStrategyFinished(true);
                } else {
                    wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(1.0, -1.0, 1.0, -1.0));
                }
                return;
            }

            if (expectedDoorMarkers.size() == 0) {
                checkedDirection++;
                isHeadRotationInProgress = true;
                headListener.onRotationChanged(directionsToCheck.get(checkedDirection));
            } else {
                isChassisRotationInProgress = true;
            }

            if (checkedDirection >= directionsToCheck.size()) {
                strategyFinishedListener.onStrategyFinished(false);
            }
        }
    }

    class AlignBetweenDoorMarkersStrategy extends BaseThroughDoorSubStrategy {

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        }
    }

    class RotateTheChassisFrontTowardsDoorStrategy extends BaseThroughDoorSubStrategy {

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        }
    }

    class DriveThroughDoorStrategy extends BaseThroughDoorSubStrategy {

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        }
    }
}
