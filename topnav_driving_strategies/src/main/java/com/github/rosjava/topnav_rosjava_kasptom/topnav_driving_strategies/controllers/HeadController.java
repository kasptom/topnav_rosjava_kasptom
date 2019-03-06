package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.JointNames;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.LinearDirectionUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.RelativeDirectionUtils;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import sensor_msgs.JointState;
import std_msgs.Float64;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.*;

public class HeadController implements IHeadController {

    private final Log log;
    private final Subscriber<std_msgs.String> drivingHeadRotationSubscriber;
    private final Subscriber<std_msgs.String> navigationHeadRotationSubscriber;
    private final Subscriber<JointState> jointStatesSubscriber;


    private boolean isIdle = true;
    private boolean isDirectionChangeNotificationRequired;
    private boolean isLinearDirectionChangeNotificationRequired;

    private RelativeDirection currentDirection = RelativeDirection.AHEAD;
    private double currentRequestedRotationDegrees = 0.0;

    private final Publisher<Float64> headRotationPublisher;
    private final Publisher<std_msgs.String> relativeDirectionChangePublisher;
    private final Publisher<std_msgs.Float64> linearDirectionChangePublisher;

    HeadController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();

        drivingHeadRotationSubscriber = connectedNode.newSubscriber(TOPNAV_STRATEGY_HEAD_DIRECTION_TOPIC, std_msgs.String._TYPE);
        navigationHeadRotationSubscriber = connectedNode.newSubscriber(TOPNAV_NAVIGATION_HEAD_DIRECTION_TOPIC, std_msgs.String._TYPE);
        jointStatesSubscriber = connectedNode.newSubscriber(CAPO_JOINT_STATES, JointState._TYPE);

        drivingHeadRotationSubscriber.addMessageListener(message -> handleStrategyHeadRotationChange(RelativeDirectionUtils.convertMessageToRelativeDirection(message)));
        navigationHeadRotationSubscriber.addMessageListener(message -> handleNavigationHeadRotationChange(RelativeDirectionUtils.convertMessageToRelativeDirection(message)));
        jointStatesSubscriber.addMessageListener(this::handleJointStateMessage);


        headRotationPublisher = connectedNode.newPublisher(HEAD_JOINT_TOPIC, Float64._TYPE);
        relativeDirectionChangePublisher = connectedNode.newPublisher(HEAD_RELATIVE_DIRECTION_CHANGE_TOPIC, std_msgs.String._TYPE);
        linearDirectionChangePublisher = connectedNode.newPublisher(HEAD_LINЕАR_DIRECTION_CHANGE_TOPIC, Float64._TYPE);
    }

    @Override
    public void handleStrategyHeadRotationChange(RelativeDirection relativeDirection) {
        currentDirection = relativeDirection;
        isDirectionChangeNotificationRequired = true;
        publishHeadRotationChange(relativeDirection);
    }

    @Override
    public void handleStrategyHeadLinearRotationChange(double rotationDegrees) {
        currentRequestedRotationDegrees = rotationDegrees;
        isLinearDirectionChangeNotificationRequired = true;
        publishHeadRotationChange(rotationDegrees);
    }

    @Override
    public void handleNavigationHeadRotationChange(RelativeDirection relativeDirection) {
        if (!isIdle) {
            return;
        }
        currentDirection = relativeDirection;
        publishHeadRotationChange(relativeDirection);
    }


    private void publishHeadRotationChange(RelativeDirection relativeDirection) {
        double rotationDegrees = RelativeDirectionUtils.toAngleDegrees(relativeDirection);
        publishHeadRotationChange(rotationDegrees);
    }

    private void publishHeadRotationChange(double rotationDegrees) {
        double rotationRads = rotationDegrees * Math.PI / 180.0;
        Float64 rotationMsg = headRotationPublisher.newMessage();
        rotationMsg.setData(rotationRads);
        headRotationPublisher.publish(rotationMsg);
    }

    private void handleJointStateMessage(JointState message) {
        int headSwivelIndex = message.getName()
                .stream()
                .filter(JointNames.JOINT_NAME_HEAD_SWIVEL::equals)
                .map(name -> message.getName().indexOf(name))
                .findFirst()
                .orElse(-1);

        if (headSwivelIndex == -1) {
            log.warn(String.format("could not find joint with name: %s", JointNames.JOINT_NAME_HEAD_SWIVEL));
            return;
        }

        double headRotationRads = message.getPosition()[headSwivelIndex];

        if (!RelativeDirectionUtils.isInPosition(headRotationRads, currentDirection)) {
            isDirectionChangeNotificationRequired = true;
        } else if (isDirectionChangeNotificationRequired) {
            log.info(String.format("sending direction change notification: %s", currentDirection.name()));
            isDirectionChangeNotificationRequired = false;
            std_msgs.String directionMessage = relativeDirectionChangePublisher.newMessage();
            directionMessage.setData(currentDirection.name());
            relativeDirectionChangePublisher.publish(directionMessage);
        }

        if (!LinearDirectionUtils.isInPosition(headRotationRads, currentRequestedRotationDegrees)) {
            isLinearDirectionChangeNotificationRequired = true;
        } else if (isLinearDirectionChangeNotificationRequired) {
            isLinearDirectionChangeNotificationRequired = false;
            Float64 linearDirectionMessage = linearDirectionChangePublisher.newMessage();
            linearDirectionMessage.setData(currentRequestedRotationDegrees);
            linearDirectionChangePublisher.publish(linearDirectionMessage);
        }
    }

    @Override
    public void onStrategyStatusChange(String strategyName) {
        isIdle = DrivingStrategy.DRIVING_STRATEGY_IDLE.equals(strategyName);
        publishHeadRotationChange(RelativeDirection.AHEAD);
    }
}
