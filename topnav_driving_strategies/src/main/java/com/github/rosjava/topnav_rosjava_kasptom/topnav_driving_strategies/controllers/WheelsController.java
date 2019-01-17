package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PassThroughDoorStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.StopBeforeWallStrategy;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;
import topnav_msgs.*;

import java.util.*;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.*;

public class WheelsController implements IWheelsController {
    private LinkedHashMap<String, Publisher<Float64>> wheelPublishersMap;

    private final Subscriber<GuidelineMsg> guidelineSubscriber;
    private final Subscriber<FeedbackMsg> markerDetectionSubscriber;
    private final Subscriber<TopNavConfigMsg> configMsgSubscriber;
    private final Subscriber<AngleRangesMsg> angleRangesMsgSubscriber;
    private final Subscriber<HoughAcc> houghAccSubscriber;

    private final HashMap<String, IDrivingStrategy> drivingStrategies = new HashMap<>();

    private Log log;
    private static final List<String> WHEEL_JOINT_NAMES = new ArrayList<>(Arrays.asList(
            "/capo_front_left_wheel_controller/command",
            "/capo_front_right_wheel_controller/command",
            "/capo_rear_left_wheel_controller/command",
            "/capo_rear_right_wheel_controller/command"));

    private WheelsVelocities currentVelocity = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);

    public WheelsController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();

        setUpJointsPublishers(connectedNode);

        configMsgSubscriber = connectedNode.newSubscriber("topnav/config", TopNavConfigMsg._TYPE);
        angleRangesMsgSubscriber = connectedNode.newSubscriber("capo/laser/angle_range", AngleRangesMsg._TYPE);
        houghAccSubscriber = connectedNode.newSubscriber("capo/laser/hough", HoughAcc._TYPE);
        markerDetectionSubscriber = connectedNode.newSubscriber("topnav/feedback", FeedbackMsg._TYPE);

        guidelineSubscriber = connectedNode.newSubscriber("topnav/guidelines", GuidelineMsg._TYPE);

        initializeDrivingStrategies(this.drivingStrategies);
        selectStrategy(DRIVING_STRATEGY_IDLE);

        guidelineSubscriber.addMessageListener(guidelineMsg -> this.selectStrategy(guidelineMsg.getGuidelineType()));

    }

    private void initializeDrivingStrategies(HashMap<String, IDrivingStrategy> drivingStrategies) {
        drivingStrategies.put(DRIVING_STRATEGY_ALONG_WALL, new DriveAlongWallStrategy(this.log));
        drivingStrategies.put(DRIVING_STRATEGY_STOP_BEFORE_WALL, new StopBeforeWallStrategy(this.log));
        drivingStrategies.put(DRIVING_STRATEGY_PASS_THROUGH_DOOR, new PassThroughDoorStrategy(this.log));
        drivingStrategies.values().forEach(strategy -> strategy.setWheelsVelocitiesListener(this::setVelocities));
    }

    public void emergencyStop() {
        log.info("removing message handlers");
        tearDownDrivingStrategy();

        log.info("stopping the robot");
        setVelocities(new WheelsVelocities(0.0, 0.0, 0.0, 0.0));
    }

    private void setUpJointsPublishers(ConnectedNode connectedNode) {
        wheelPublishersMap = new LinkedHashMap<>();
        for (String topicName : WHEEL_JOINT_NAMES) {
            wheelPublishersMap.put(topicName, connectedNode.newPublisher(topicName, Float64._TYPE));
        }
    }

    private void setUpDrivingStrategy(IDrivingStrategy drivingStrategy) {
        drivingStrategy.setWheelsVelocitiesListener(this::setVelocities);

        configMsgSubscriber.addMessageListener(drivingStrategy::handleConfigMessage);
        angleRangesMsgSubscriber.addMessageListener(drivingStrategy::handleAngleRangeMessage);
        houghAccSubscriber.addMessageListener(drivingStrategy::handleHoughAccMessage);
        markerDetectionSubscriber.addMessageListener(drivingStrategy::handleDetectionMessage);
    }

    private void selectStrategy(String strategyName) {
        tearDownDrivingStrategy();

        if (DRIVING_STRATEGY_IDLE.equals(strategyName)) {
            log.info("Set to idle state");
            setVelocities(new WheelsVelocities(0.0, 0.0, 0.0, 0.0));
            return;
        }

        if (!drivingStrategies.containsKey(strategyName)) {
            log.info(String.format("Strategy %s not found", strategyName));
            return;
        }

        setUpDrivingStrategy(drivingStrategies.get(strategyName));
    }

    private void tearDownDrivingStrategy() {
        this.configMsgSubscriber.removeAllMessageListeners();
        this.angleRangesMsgSubscriber.removeAllMessageListeners();
        this.houghAccSubscriber.removeAllMessageListeners();
        this.markerDetectionSubscriber.removeAllMessageListeners();
    }

    private void setVelocities(WheelsVelocities wheelsVelocities) {
        double[] velocities = new double[]{
                wheelsVelocities.getFrontLeft(),
                wheelsVelocities.getFrontRight(),
                wheelsVelocities.getRearLeft(),
                wheelsVelocities.getRearRight()
        };

        if (isVelocityChanged(wheelsVelocities, currentVelocity)) {
            log.info(String.format("Setting velocities (%.2f, %.2f,%.2f, %.2f)",
                    velocities[0], velocities[1], velocities[2], velocities[3]));
        }

        List<Float64> messages = new ArrayList<>();
        int i = 0;

        for (String topicName : wheelPublishersMap.keySet()) {
            Float64 wheelVelocityMsg = wheelPublishersMap.get(topicName).newMessage();
            wheelVelocityMsg.setData(velocities[i++]);
            messages.add(wheelVelocityMsg);
        }

        i = 0;
        for (String topicName : wheelPublishersMap.keySet()) {
            wheelPublishersMap.get(topicName).publish(messages.get(i++));
        }
    }

    private boolean isVelocityChanged(WheelsVelocities wheelsVelocities, WheelsVelocities currentVelocity) {
        return wheelsVelocities.getRearLeft() != currentVelocity.getRearLeft()
                || wheelsVelocities.getRearRight() != currentVelocity.getRearRight()
                || wheelsVelocities.getFrontLeft() != currentVelocity.getFrontLeft()
                || wheelsVelocities.getFrontRight() != currentVelocity.getFrontRight();
    }
}
