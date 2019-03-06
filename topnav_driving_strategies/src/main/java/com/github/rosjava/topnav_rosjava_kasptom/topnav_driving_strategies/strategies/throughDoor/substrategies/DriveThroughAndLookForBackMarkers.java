package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder.DoorFinder;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopologyMsg;

import java.util.HashMap;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.BASE_ROBOT_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class DriveThroughAndLookForBackMarkers extends BaseSubStrategy {

    private final Log log;
    private boolean isBackMarkVisible = false;
        private DoorFinder doorFinder = new DoorFinder();
        private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();

        public DriveThroughAndLookForBackMarkers(WheelsVelocitiesChangeListener wheelsListener,
                                                 HeadRotationChangeRequestListener headListener,
                                                 SubStrategyListener subStrategyListener,
                                                 StrategyFinishedListener strategyFinishedListener,
                                                 HashMap<String, GuidelineParam> guidelineParamsMap, Log log) {
            super(wheelsListener, headListener, subStrategyListener, strategyFinishedListener, guidelineParamsMap);
            this.log = log;
        }

        @Override
        public void handleHoughAccMessage(HoughAcc houghAcc) {
        }

        @Override
        public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
            if (isBackMarkVisible) {
                return;
            }

            doorFinder.dividePointsToClusters(angleRangesMsg);
            DoorFinder.Point midPoint = null;
            try {
                midPoint = doorFinder.getClustersMidPoint();
            } catch (DoorFinder.PointNotFoundException pointNotFoundException) {
                log.info("Could not find the mid point");
            }

            if (midPoint == null) {
                wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
                return;
            }

            double range = Math.sqrt(Math.pow(midPoint.getX(), 2) + Math.pow(midPoint.getY(), 2));
            double angleRads = range != 0
                    ? Math.asin(midPoint.getX() / range)
                    : 0.0;
            double angleDegrees = angleRads / 180.0 * Math.PI;

            WheelsVelocities velocities = velocityCalculator.calculateRotationSpeed(angleDegrees, 0.0, System.nanoTime(), 0.0, 0.0);
            velocities = WheelsVelocities.addVelocities(BASE_ROBOT_VELOCITY, velocities);

            wheelsListener.onWheelsVelocitiesChanged(velocities);
        }

        @Override
        public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
            if (isBackMarkVisible) {
                finishListener.onStrategyFinished(true);
                return;
            }

            List<TopologyMsg> topologyMsgs = PassThroughDoorUtils.findBackDoorMarkers(feedbackMsg, guidelineParamsMap);
            isBackMarkVisible = !topologyMsgs.isEmpty();
        }
    }