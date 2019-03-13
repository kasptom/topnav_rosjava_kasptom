package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.*;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeAlignment;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopologyMsg;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class PassThroughDoorStrategy extends BaseCompoundStrategy implements IDrivingStrategy {

    public PassThroughDoorStrategy(Log log) {
        super(log);
    }

    @Override
    public void initializeSubStrategies() {
        this.subStrategies = new HashMap<>();
        this.subStrategies.put(DETECT_MARKER, new RotateTheChassisSideTowardsDoorStrategy(wheelsListener, headListener, this, strategyFinishedListener, log, guidelineParamsMap));
        this.subStrategies.put(ALIGN_BETWEEN_DOOR, new AlignBetweenDoorMarkersStrategy(wheelsListener, guidelineParamsMap));
        this.subStrategies.put(ROTATE_FRONT_AGAINST_DOOR, new RotateTheChassisFrontTowardsDoorStrategy(wheelsListener, guidelineParamsMap));
        this.subStrategies.put(DRIVE_THROUGH_DOOR, new DriveThroughAndLookForBackMarkers(wheelsListener, headListener, this, strategyFinishedListener, guidelineParamsMap, log));
    }

    @Override
    public CompoundStrategyStage[] getSubStrategiesExecutionOrder() {
        return new CompoundStrategyStage[]{DETECT_MARKER, ALIGN_BETWEEN_DOOR, ROTATE_FRONT_AGAINST_DOOR, DRIVE_THROUGH_DOOR};
    }

    @Override
    public void startStrategy() {
        initializeSubStrategies();
        switchToInitialStage(AT_LEFT); // TODO possiblity to set AT_RIGHT
    }

    class AlignBetweenDoorMarkersStrategy extends BaseSubStrategy {

        AlignBetweenDoorMarkersStrategy(WheelsVelocitiesChangeListener wheelsListener, HashMap<String, GuidelineParam> guidelineParamsMap) {
            super(wheelsListener, headListener, PassThroughDoorStrategy.this, strategyFinishedListener, guidelineParamsMap);
        }

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
                subStrategyListener.onStageFinished(ALIGN_BETWEEN_DOOR, AHEAD);
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

        RotateTheChassisFrontTowardsDoorStrategy(WheelsVelocitiesChangeListener wheelsListener, HashMap<String, GuidelineParam> guidelineParamsMap) {
            super(wheelsListener, headListener, PassThroughDoorStrategy.this, strategyFinishedListener, guidelineParamsMap);
        }

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
                wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
                log.info("rotated front towards the door");
                subStrategyListener.onStageFinished(ROTATE_FRONT_AGAINST_DOOR, BEHIND);
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
}
