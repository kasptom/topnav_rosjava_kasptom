package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.ArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.AruCoTrackerTestStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.FollowWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.StopBeforeWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorStrategyV2;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;
import topnav_msgs.*;

import java.util.HashMap;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.*;

public class MainController implements IMainController {

    private final IHeadController headController;
    private final IWheelsController wheelsController;
    private final IArUcoHeadTracker arUcoHeadTracker;

    private final Subscriber<GuidelineMsg> guidelineSubscriber;
    private final Subscriber<FeedbackMsg> markerDetectionSubscriber;
    private final Subscriber<TopNavConfigMsg> configMsgSubscriber;
    private final Subscriber<AngleRangesMsg> angleRangesMsgSubscriber;
    private final Subscriber<HoughAcc> houghAccSubscriber;
    private final TopnavSubscriber<MarkersMsg> arUcoSubscriber;
    private final Subscriber<std_msgs.String> headDirectionChangeSubscriber;
    private final Subscriber<Float64> headLinearDirectionChangeSubscriber;

    private final HashMap<String, IDrivingStrategy> drivingStrategies = new HashMap<>();
    private final HashMap<String, IArUcoHeadTracker.TrackedMarkerListener> trackedMarkerListeners = new HashMap<>();

    private Log log;

    public MainController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();

        headController = new HeadController(connectedNode);
        wheelsController = new WheelsController(connectedNode);
        arUcoHeadTracker = new ArUcoHeadTracker(log);

        configMsgSubscriber = connectedNode.newSubscriber("/topnav/config", TopNavConfigMsg._TYPE);
        angleRangesMsgSubscriber = connectedNode.newSubscriber(TopicNames.TOPNAV_ANGLE_RANGE_TOPIC, AngleRangesMsg._TYPE);
        houghAccSubscriber = connectedNode.newSubscriber("/capo/laser/hough", HoughAcc._TYPE);
        markerDetectionSubscriber = connectedNode.newSubscriber("/topnav/feedback", FeedbackMsg._TYPE);

        arUcoSubscriber = new TopnavSubscriber<>(connectedNode, TopicNames.TOPNAV_ARUCO_TOPIC, MarkersMsg._TYPE);
        arUcoHeadTracker.setAngleChangeListener(headController::handleStrategyHeadLinearRotationChange);

        guidelineSubscriber = connectedNode.newSubscriber("/topnav/guidelines", GuidelineMsg._TYPE);
        headDirectionChangeSubscriber = connectedNode.newSubscriber(TopicNames.HEAD_RELATIVE_DIRECTION_CHANGE_TOPIC, std_msgs.String._TYPE);

        headLinearDirectionChangeSubscriber = connectedNode.newSubscriber(TopicNames.HEAD_LINЕАR_DIRECTION_CHANGE_TOPIC, std_msgs.Float64._TYPE);


        initializeDrivingStrategies(drivingStrategies, trackedMarkerListeners);
        selectStrategy(DRIVING_STRATEGY_IDLE, null);

        guidelineSubscriber.addMessageListener(guidelineMsg -> selectStrategy(guidelineMsg.getGuidelineType(), guidelineMsg.getParameters()));
    }

    private void initializeDrivingStrategies(HashMap<String, IDrivingStrategy> drivingStrategies, HashMap<String, IArUcoHeadTracker.TrackedMarkerListener> trackedMarkerListeners) {
        drivingStrategies.put(DRIVING_STRATEGY_ALONG_WALL, new DriveAlongWallStrategy(log));
        drivingStrategies.put(DRIVING_STRATEGY_ALONG_WALL_2, new FollowWallStrategy(log));
        drivingStrategies.put(DRIVING_STRATEGY_STOP_BEFORE_WALL, new StopBeforeWallStrategy(log));
        drivingStrategies.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR, new PassThroughDoorStrategy(log));
        drivingStrategies.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2, new PassThroughDoorStrategyV2(arUcoHeadTracker, log));
        drivingStrategies.put(DRIVING_STRATEGY_TRACK_ARUCOS, new AruCoTrackerTestStrategy(arUcoHeadTracker));
        drivingStrategies.values().forEach(strategy -> strategy.setWheelsVelocitiesListener(wheelsController::setVelocities));

        trackedMarkerListeners.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2, (PassThroughDoorStrategyV2) drivingStrategies.get(DRIVING_STRATEGY_PASS_THROUGH_DOOR_2));
        trackedMarkerListeners.put(DRIVING_STRATEGY_TRACK_ARUCOS, (AruCoTrackerTestStrategy) drivingStrategies.get(DRIVING_STRATEGY_TRACK_ARUCOS));
    }

    public void emergencyStop() {
        log.info("removing message handlers");
        tearDownDrivingStrategy();

        log.info("stopping the robot");
        wheelsController.setVelocities(new WheelsVelocities(0.0, 0.0, 0.0, 0.0));
    }

    private void selectStrategy(String strategyName, List<String> parameters) {
        tearDownDrivingStrategy();
        tearDownArUcoListeners();
        headController.onStrategyStatusChange(strategyName);

        log.info(String.format("Selecting %s strategy", strategyName));
        if (DRIVING_STRATEGY_IDLE.equals(strategyName)) {
            log.info("Set to idle state");
            wheelsController.setVelocities(new WheelsVelocities(0.0, 0.0, 0.0, 0.0));
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


    private void setUpDrivingStrategy(IDrivingStrategy drivingStrategy, List<String> parameters) {
        drivingStrategy.setWheelsVelocitiesListener(wheelsController::setVelocities);
        drivingStrategy.setHeadRotationChangeListener(headController::handleStrategyHeadRotationChange);
        drivingStrategy.setStrategyFinishedListener(isSuccess -> selectStrategy(DRIVING_STRATEGY_IDLE, null));
        drivingStrategy.setGuidelineParameters(parameters);

        drivingStrategy.startStrategy();

        configMsgSubscriber.addMessageListener(drivingStrategy::handleConfigMessage);
        angleRangesMsgSubscriber.addMessageListener(drivingStrategy::handleAngleRangeMessage);
        houghAccSubscriber.addMessageListener(drivingStrategy::handleHoughAccMessage);
        markerDetectionSubscriber.addMessageListener(drivingStrategy::handleDetectionMessage);
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
        arUcoHeadTracker.setTrackedMarkerListener(null);
    }

    private void setUpArUcoListeners(IArUcoHeadTracker.TrackedMarkerListener arUcoMessageListener) {
        arUcoHeadTracker.setTrackedMarkerListener(arUcoMessageListener);
        arUcoHeadTracker.setAngleChangeListener(headController::handleStrategyHeadLinearRotationChange);

        arUcoSubscriber.addMessageListener(arUcoHeadTracker::handleArUcoMessage);
        headLinearDirectionChangeSubscriber.addMessageListener(arUcoHeadTracker::handleHeadRotationChange);
    }
}
