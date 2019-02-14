package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.AngleRange;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeAlignment;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.*;

import java.util.Arrays;
import java.util.Comparator;
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
        guidelineParamsMap = new HashMap<>();
        substrategies = initializeSubstrategies();
    }

    private void setCurrentStage(ThroughDoorStage stage, RelativeDirection direction) {
        currentStage = stage;
        isHeadRotationInProgress = true;
        headListener.onRotationChanged(direction);
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
        initializeSubstrategies();
        setCurrentStage(DETECTED_MARKER, AT_LEFT); // TODO possiblity to set AT_RIGHT
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
            List<TopologyMsg> expectedDoorMarkers = findDoorFrontMakers(feedbackMsg);

            if (isChassisRotationInProgress) {
                if (expectedDoorMarkers.size() > 0) {
                    wheelsListener.onWheelsVelocitiesChanged(WheelsVelocityConstants.ZERO_VELOCITY);
                    log.info("rotated side towards the door");
                    setCurrentStage(ROTATED_SIDE_TOWARDS_DOOR, AT_LEFT);
                } else {
                    wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(1.5, -1.5, 1.5, -1.5));
                }
                return;
            }

            if (expectedDoorMarkers.size() == 0) {
                checkedDirection++;
                isHeadRotationInProgress = true;
            } else {
                isChassisRotationInProgress = true;
            }

            if (checkedDirection >= directionsToCheck.size()) {
                strategyFinishedListener.onStrategyFinished(false);
            } else {
                headListener.onRotationChanged(directionsToCheck.get(checkedDirection));
            }
        }
    }

    private List<TopologyMsg> findDoorFrontMakers(FeedbackMsg feedbackMsg) {
        String leftMarkerId = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID).getValue();
        String rightMarkerId = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue();

        return feedbackMsg.getTopologies()
                .stream()
                .filter(topologyMsg -> topologyMsg.getIdentity().equals(leftMarkerId)
                        || topologyMsg.getIdentity().equals(rightMarkerId))
                .collect(Collectors.toList());
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
            List<TopologyMsg> expectedDoorMarkers = findDoorFrontMakers(feedbackMsg);
            if (expectedDoorMarkers.size() == 0) {
                strategyFinishedListener.onStrategyFinished(false);
                return;
            }

            final double[] velocity = {0.0};
            expectedDoorMarkers.forEach(marker -> velocity[0] = setVelocityAccordingToDoorPosition(marker));

            if (velocity[0] == 0) {
                setCurrentStage(ALIGNED_WITH_DOOR, AHEAD);
            }

            wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(velocity[0], velocity[0], velocity[0], velocity[0]));
        }

        private double setVelocityAccordingToDoorPosition(TopologyMsg marker) {
            // FIXME: what robot is comming from the leftwise side of the door?
            double velocity = 0.0;
            if (marker.getIdentity().equals(guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID).getValue())) {
                if (marker.getRelativeAlignment().equals(RelativeAlignment.CENTER.name())
                        || marker.getRelativeAlignment().equals(RelativeAlignment.RIGHT.name())) {
                    velocity = 1.0;
                }
            }

            if (marker.getIdentity().equals(guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue())) {
                if (marker.getRelativeAlignment().equals(RelativeAlignment.CENTER.name())
                        || marker.getRelativeAlignment().equals(RelativeAlignment.LEFT.name())) {
                    velocity = -1.0;
                }
            }
            return velocity;
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
            List<TopologyMsg> expectedDoorMarkers = findRightFrontMarker(feedbackMsg);
            if (expectedDoorMarkers.size() > 0) {
                wheelsListener.onWheelsVelocitiesChanged(WheelsVelocityConstants.ZERO_VELOCITY);
                log.info("rotated front towards the door");
                setCurrentStage(ROTATED_TOWARDS_DOOR, BEHIND);
            } else {
                wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(1.5, -1.5, 1.5, -1.5));
            }
        }

        private List<TopologyMsg> findRightFrontMarker(FeedbackMsg feedbackMsg) {
            String rightMarkerId = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue();

            return feedbackMsg.getTopologies()
                    .stream()
                    .filter(topologyMsg -> topologyMsg.getIdentity().equals(rightMarkerId))
                    .collect(Collectors.toList());
        }
    }

    class DriveThroughDoorStrategy extends BaseThroughDoorSubStrategy {

        private boolean isBackMarkVisible = false;

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
            if (isBackMarkVisible) {
                return;
            }

            List<AngleRange> angleRanges = AngleRange.messageToAngleRange(angleRangesMsg);
            AngleRange closestOfTheRight = angleRanges
                    .stream()
                    .filter(angleRange -> angleRange.getAngleRad() >= 0)
                    .min(Comparator.comparingDouble(AngleRange::getRange))
                    .orElse(new AngleRange(0.0, Double.MAX_VALUE));

            AngleRange closestOfTheLeft = angleRanges
                    .stream()
                    .filter(angleRange -> angleRange.getAngleRad() <= 0)
                    .min(Comparator.comparingDouble(AngleRange::getRange))
                    .orElse(new AngleRange(0.0, Double.MAX_VALUE));

            double leftClosest = closestOfTheLeft.getRange();
            double rightClosest = closestOfTheRight.getRange();
            double avgClosest = (leftClosest + rightClosest) / 2.0;

            double MAX_VELOCITY = 2.0;

            double leftVelocity = leftClosest > avgClosest
                    ? MAX_VELOCITY
                    : (leftClosest / rightClosest) * MAX_VELOCITY;

            double rightVelocity = rightClosest > avgClosest
                    ? MAX_VELOCITY
                    : (rightClosest / leftClosest) * MAX_VELOCITY;

            wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(leftVelocity, rightVelocity, leftVelocity, rightVelocity));
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
            if (isBackMarkVisible) {
                strategyFinishedListener.onStrategyFinished(true);
                return;
            }

            List<TopologyMsg> topologyMsgs = findDoorBackMarkers(feedbackMsg);
            isBackMarkVisible = !topologyMsgs.isEmpty();
        }

        private List<TopologyMsg> findDoorBackMarkers(FeedbackMsg feedbackMsg) {
            String leftBackMarker = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_BACK_LEFT_MARKER_ID).getValue();
            String rightBackMarker = guidelineParamsMap.get(DrivingStrategy.ThroughDoor.KEY_BACK_RIGHT_MARKER_ID).getValue();

            return feedbackMsg.getTopologies()
                    .stream()
                    .filter(topologyMsg ->
                        topologyMsg.getIdentity().equals(leftBackMarker) || topologyMsg.getIdentity().equals(rightBackMarker))
                    .collect(Collectors.toList());
        }
    }
}
