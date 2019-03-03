package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.BaseSubStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.RotateTheChassisSideTowardsDoorStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.SubStrategyListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.*;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.DoorFinder;
import org.apache.commons.logging.Log;
import topnav_msgs.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.DOOR_DETECTION_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.MAX_VELOCITY_DELTA;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class PassThroughDoorStrategy extends BasePassThroughDoorStrategy implements IDrivingStrategy, SubStrategyListener {
    public PassThroughDoorStrategy(Log log) {
        super(log);
        guidelineParamsMap = new HashMap<>();
        substrategies = initializeSubStrategies();
    }

    private void setCurrentStage(ThroughDoorStage stage, RelativeDirection direction) {
        log.info(String.format("Setting current stage to %s", stage));
        currentStage = stage;

        isHeadRotationInProgress = true;
        headListener.onRotationChanged(direction);
    }

    private HashMap<ThroughDoorStage, IDrivingStrategy> initializeSubStrategies() {
        this.substrategies = new HashMap<>();
        this.substrategies.put(DETECT_MARKER, new RotateTheChassisSideTowardsDoorStrategy(wheelsListener, headListener, this, strategyFinishedListener, log));
        this.substrategies.put(ALIGN_BETWEEN_DOOR, new AlignBetweenDoorMarkersStrategy());
        this.substrategies.put(ROTATE_FRONT_AGAINST_DOOR, new RotateTheChassisFrontTowardsDoorStrategy());
        this.substrategies.put(DRIVE_THROUGH_DOOR, new DriveStrategy());
        return substrategies;
    }

    @Override
    public void startStrategy() {
        initializeSubStrategies();
        setCurrentStage(DETECT_MARKER, AT_LEFT); // TODO possiblity to set AT_RIGHT
    }

    @Override
    public void onStageChanged(ThroughDoorStage stage, RelativeDirection direction) {
        setCurrentStage(stage, direction);
    }

    @Override
    public void headRotationInProgress(boolean isInProgress) {
        isHeadRotationInProgress = isInProgress;
    }

    class AlignBetweenDoorMarkersStrategy extends BaseSubStrategy {

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {

        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
            List<TopologyMsg> expectedDoorMarkers = PassThroughDoorUtils.findFrontDoorMarkers(feedbackMsg, guidelineParamsMap);
            if (expectedDoorMarkers.size() == 0) {
                strategyFinishedListener.onStrategyFinished(false);
                return;
            }

            final double[] velocity = {0.0};
            expectedDoorMarkers.forEach(marker -> velocity[0] = setVelocityAccordingToDoorPosition(marker));

            if (velocity[0] == 0) {
                setCurrentStage(ROTATE_FRONT_AGAINST_DOOR, AHEAD);
            }

            wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(velocity[0], velocity[0], velocity[0], velocity[0]));
        }

        private double setVelocityAccordingToDoorPosition(TopologyMsg marker) {
            // FIXME: what if robot is coming from the left side of the door?
            double velocity = 0.0;
            if (marker.getIdentity().equals(guidelineParamsMap.get(KEY_FRONT_LEFT_MARKER_ID).getValue())) {
                if (marker.getRelativeAlignment().equals(RelativeAlignment.CENTER.name())
                        || marker.getRelativeAlignment().equals(RelativeAlignment.RIGHT.name())) {
                    velocity = 1.0;
                }
            }

            if (marker.getIdentity().equals(guidelineParamsMap.get(KEY_FRONT_RIGHT_MARKER_ID).getValue())) {
                if (marker.getRelativeAlignment().equals(RelativeAlignment.CENTER.name())
                        || marker.getRelativeAlignment().equals(RelativeAlignment.LEFT.name())) {
                    velocity = -1.0;
                }
            }
            return velocity;
        }
    }

    class RotateTheChassisFrontTowardsDoorStrategy extends BaseSubStrategy {

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
                setCurrentStage(DRIVE_THROUGH_DOOR, BEHIND);
            } else {
                wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(1.5, -1.5, 1.5, -1.5));
            }
        }

        private List<TopologyMsg> findRightFrontMarker(FeedbackMsg feedbackMsg) {
            String rightMarkerId = guidelineParamsMap.get(KEY_FRONT_RIGHT_MARKER_ID).getValue();

            return feedbackMsg.getTopologies()
                    .stream()
                    .filter(topologyMsg -> topologyMsg.getIdentity().equals(rightMarkerId))
                    .collect(Collectors.toList());
        }
    }

    class DriveStrategy extends BaseSubStrategy {

        private boolean isBackMarkVisible = false;
        private DoorFinder doorFinder = new DoorFinder();

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
            if (isBackMarkVisible) {
                return;
            }

            this.doorFinder.dividePointsToClusters(angleRangesMsg);
            DoorFinder.Point midPoint = this.doorFinder.getClustersMidPoint();
            double MAX_VELOCITY = 2.0;

            double leftVelocity = MAX_VELOCITY + MAX_VELOCITY_DELTA * (-midPoint.getX() / DOOR_DETECTION_RANGE);
            double rightVelocity = MAX_VELOCITY + MAX_VELOCITY_DELTA * (midPoint.getX() / DOOR_DETECTION_RANGE);

            wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(leftVelocity, rightVelocity, leftVelocity, rightVelocity));
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
            if (isBackMarkVisible) {
                strategyFinishedListener.onStrategyFinished(true);
                return;
            }

            List<TopologyMsg> topologyMsgs = PassThroughDoorUtils.findBackDoorMarkers(feedbackMsg, guidelineParamsMap);
            isBackMarkVisible = !topologyMsgs.isEmpty();
        }
    }
}
