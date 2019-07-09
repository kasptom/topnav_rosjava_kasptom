package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.ArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.ManualSteeringController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions.IReactionController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions.IReactionStartListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions.ReactionController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.AruCoTrackerTestStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DeadReckoningTestStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.FollowWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.StopBeforeWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.approachMarker.ApproachMarkerStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.markerRelativePositioning.PositionAccordingToMarkerStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorStrategyV2;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;
import std_msgs.Int16;
import std_msgs.UInt64;
import topnav_msgs.*;

import java.util.HashMap;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class MainController implements IMainController {

    private final IHeadController headController;
    private final IWheelsController wheelsController;
    private final IArUcoHeadTracker arUcoHeadTracker;
    private final IReactionController reactionController;

    private final Publisher<std_msgs.String> strategyFinishedPublisher;

    private final Subscriber<GuidelineMsg> guidelineSubscriber;
    private final Subscriber<FeedbackMsg> markerDetectionSubscriber;
    private final Subscriber<TopNavConfigMsg> configMsgSubscriber;
    private final Subscriber<AngleRangesMsg> angleRangesMsgSubscriber;
    private final Subscriber<HoughAcc> houghAccSubscriber;
    private final TopnavSubscriber<MarkersMsg> arUcoSubscriber;
    private final Subscriber<std_msgs.String> headDirectionChangeSubscriber;
    private final Subscriber<Float64> headLinearDirectionChangeSubscriber;
    private final Subscriber<std_msgs.UInt64> headTimeSinceLastRotationSubscriber;
    private final Subscriber<std_msgs.Int16> manualSteeringSubscriber;
    private final ManualSteeringController manualSteeringController;

    private final HashMap<String, IDrivingStrategy> drivingStrategies = new HashMap<>();
    private final HashMap<String, IArUcoHeadTracker.TrackedMarkerListener> trackedMarkerListeners = new HashMap<>();

    private TopNavConfigMsg latestConfigMessage = null;

    private Log log;

    public MainController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();

        headController = new HeadController(connectedNode);
        wheelsController = new WheelsController(connectedNode);
        arUcoHeadTracker = new ArUcoHeadTracker(log);
        reactionController = new ReactionController(connectedNode);
        manualSteeringController = new ManualSteeringController();

        strategyFinishedPublisher = connectedNode.newPublisher(TOPNAV_STRATEGY_CHANGE_TOPIC, std_msgs.String._TYPE);

        configMsgSubscriber = connectedNode.newSubscriber(TOPNAV_CONFIG_TOPIC, TopNavConfigMsg._TYPE);
        angleRangesMsgSubscriber = connectedNode.newSubscriber(TOPNAV_ANGLE_RANGE_TOPIC, AngleRangesMsg._TYPE);
        houghAccSubscriber = connectedNode.newSubscriber(TOPNAV_HOUGH_TOPIC, HoughAcc._TYPE);
        markerDetectionSubscriber = connectedNode.newSubscriber(TOPNAV_FEEDBACK_TOPIC, FeedbackMsg._TYPE);

        arUcoSubscriber = new TopnavSubscriber<>(connectedNode, TOPNAV_ARUCO_TOPIC, MarkersMsg._TYPE);
        arUcoHeadTracker.setAngleChangeListener(headController::handleStrategyHeadLinearRotationChange);

        guidelineSubscriber = connectedNode.newSubscriber(TOPNAV_GUIDELINES_TOPIC, GuidelineMsg._TYPE);

        headDirectionChangeSubscriber = connectedNode.newSubscriber(HEAD_RELATIVE_DIRECTION_CHANGE_TOPIC, std_msgs.String._TYPE);
        headLinearDirectionChangeSubscriber = connectedNode.newSubscriber(HEAD_LINEAR_DIRECTION_CHANGE_TOPIC, std_msgs.Float64._TYPE);
        headTimeSinceLastRotationSubscriber = connectedNode.newSubscriber(HEAD_TIME_MS_SINCE_LAST_ROTATION_TOPIC, UInt64._TYPE);

        manualSteeringSubscriber = connectedNode.newSubscriber(TOPNAV_NAVIGATION_MANUAL_STEERING_TOPIC, std_msgs.Int16._TYPE);
        initializeManualSteering(manualSteeringSubscriber);

        initializeDrivingStrategies(drivingStrategies, trackedMarkerListeners);
        selectStrategy(DRIVING_STRATEGY_IDLE, null);
        reactionController.setWheelsVelocitiesListener(wheelsController::setVelocities);

        guidelineSubscriber.addMessageListener(guidelineMsg -> selectStrategy(guidelineMsg.getGuidelineType(), guidelineMsg.getParameters()));
    }

    private void initializeManualSteering(Subscriber<Int16> manualSteeringSubscriber) {
        manualSteeringSubscriber.addMessageListener(message -> {
            WheelsVelocities velocities = manualSteeringController.handleSteeringMessage(message.getData());
            wheelsController.setVelocities(velocities);
        });
    }

    private void initializeDrivingStrategies(HashMap<String, IDrivingStrategy> drivingStrategies,
                                             HashMap<String, IArUcoHeadTracker.TrackedMarkerListener> trackedMarkerListeners) {
        drivingStrategies.put(DRIVING_STRATEGY_ALONG_WALL_2, new FollowWallStrategy((IReactionStartListener) reactionController, log));
        drivingStrategies.put(DRIVING_STRATEGY_STOP_BEFORE_WALL, new StopBeforeWallStrategy(log));
        drivingStrategies.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2, new PassThroughDoorStrategyV2(arUcoHeadTracker, log));
        drivingStrategies.put(DRIVING_STRATEGY_APPROACH_MARKER, new ApproachMarkerStrategy(arUcoHeadTracker, log));
        drivingStrategies.put(DRIVING_STRATEGY_TRACK_ARUCOS, new AruCoTrackerTestStrategy(arUcoHeadTracker));
        drivingStrategies.put(DRIVING_STRATEGY_DEAD_RECKONING_TEST, new DeadReckoningTestStrategy());
        drivingStrategies.put(DRIVING_STRATEGY_ACCORDING_TO_MARKER, new PositionAccordingToMarkerStrategy(arUcoHeadTracker, log));
        drivingStrategies.values().forEach(strategy -> strategy.setWheelsVelocitiesListener(wheelsController::setVelocities));

        trackedMarkerListeners.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2, (PassThroughDoorStrategyV2) drivingStrategies.get(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2));
        trackedMarkerListeners.put(DRIVING_STRATEGY_APPROACH_MARKER, (ApproachMarkerStrategy) drivingStrategies.get(DRIVING_STRATEGY_APPROACH_MARKER));
        trackedMarkerListeners.put(DRIVING_STRATEGY_TRACK_ARUCOS, (AruCoTrackerTestStrategy) drivingStrategies.get(DRIVING_STRATEGY_TRACK_ARUCOS));
        trackedMarkerListeners.put(DRIVING_STRATEGY_ACCORDING_TO_MARKER, (PositionAccordingToMarkerStrategy) drivingStrategies.get(DRIVING_STRATEGY_ACCORDING_TO_MARKER));
    }

    public void emergencyStop() {
        log.info("removing message handlers");
        tearDownDrivingStrategy();
        tearDownArUcoListeners();

        log.info("stopping the robot");
        wheelsController.setVelocities(ZERO_VELOCITY);
    }

    private void selectStrategy(String strategyName, List<String> parameters) {
        tearDownDrivingStrategy();
        tearDownArUcoListeners();

        publishStrategyChangeMessage(strategyName);

        headController.onStrategyStatusChange(strategyName);

        log.info(String.format("Selecting %s strategy", strategyName));
        if (DRIVING_STRATEGY_IDLE.equals(strategyName)) {
            log.info("Set to idle state");
            wheelsController.setVelocities(ZERO_VELOCITY);
            return;
        }

        if (!drivingStrategies.containsKey(strategyName)) {
            log.info(String.format("Strategy %s not found", strategyName));
            return;
        }

        if (trackedMarkerListeners.containsKey(strategyName)) {
            setUpArUcoListeners(trackedMarkerListeners.get(strategyName));
        }

        setUpDrivingStrategy(drivingStrategies.get(strategyName), parameters);
    }

    private void publishStrategyChangeMessage(String strategyName) {
        std_msgs.String message = strategyFinishedPublisher.newMessage();
        message.setData(strategyName);
        strategyFinishedPublisher.publish(message);
    }


    private void setUpDrivingStrategy(IDrivingStrategy drivingStrategy, List<String> parameters) {
        drivingStrategy.setWheelsVelocitiesListener(wheelsController::setVelocities);
        drivingStrategy.setHeadRotationChangeListener(headController::handleStrategyHeadRotationChange);
        drivingStrategy.setStrategyFinishedListener(isSuccess -> {
            System.out.printf("Strategy finished with %s\n", isSuccess ? "success" : "failure");
            selectStrategy(DRIVING_STRATEGY_IDLE, null);
        });
        drivingStrategy.setGuidelineParameters(parameters);

        drivingStrategy.startStrategy();

        configMsgSubscriber.addMessageListener(drivingStrategy::handleConfigMessage);

        angleRangesMsgSubscriber.addMessageListener(message -> {
            if (reactionController.isReactionInProgress()) {
                reactionController.onAngleRangeMessage(message);
                return;
            }
            drivingStrategy.handleAngleRangeMessage(message);
        });

        houghAccSubscriber.addMessageListener(message -> {
            if (reactionController.isReactionInProgress()) {
                reactionController.onHoughAccMessage(message);
                return;
            }
            drivingStrategy.handleHoughAccMessage(message);
        });

        markerDetectionSubscriber.addMessageListener(message -> {
            if (reactionController.isReactionInProgress()) return;
            drivingStrategy.handleDetectionMessage(message);
        });
        headDirectionChangeSubscriber.addMessageListener(drivingStrategy::handleHeadDirectionChange);
    }

    private void tearDownDrivingStrategy() {
        configMsgSubscriber.removeAllMessageListeners();
        angleRangesMsgSubscriber.removeAllMessageListeners();
        houghAccSubscriber.removeAllMessageListeners();
        markerDetectionSubscriber.removeAllMessageListeners();
        headDirectionChangeSubscriber.removeAllMessageListeners();
        arUcoHeadTracker.stop();
    }

    private void tearDownArUcoListeners() {
        arUcoSubscriber.removeAllLocalMessageListeners();
        headLinearDirectionChangeSubscriber.removeAllMessageListeners();
        headTimeSinceLastRotationSubscriber.removeAllMessageListeners();
        arUcoHeadTracker.setTrackedMarkerListener(null);
    }

    private void setUpArUcoListeners(IArUcoHeadTracker.TrackedMarkerListener arUcoMessageListener) {
        arUcoHeadTracker.setTrackedMarkerListener(arUcoMessageListener);
        arUcoHeadTracker.setAngleChangeListener(headController::handleStrategyHeadLinearRotationChange);

        arUcoSubscriber.addMessageListener(arUcoHeadTracker::handleArUcoMessage);
        headLinearDirectionChangeSubscriber.addMessageListener(arUcoHeadTracker::handleHeadRotationChange);
        headTimeSinceLastRotationSubscriber.addMessageListener(arUcoHeadTracker::handleTimeSinceLastRotationMessage);
    }
}
