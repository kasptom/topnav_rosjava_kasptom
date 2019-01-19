package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.JointNames;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
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

    private RelativeDirection currentDirection = RelativeDirection.AHEAD;

    private final Publisher<Float64> headRotationPublisher;
    private final Publisher<std_msgs.String> relativeDirectionChangePublisher;

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
    }

    @Override
    public void handleStrategyHeadRotationChange(RelativeDirection relativeDirection) {
        currentDirection = relativeDirection;
        publishHeadRotationChange(relativeDirection);
    }

    @Override
    public void handleNavigationHeadRotationChange(RelativeDirection relativeDirection) {
        if (!isIdle) {
            return;
        }
        currentDirection = relativeDirection;
        publishHeadRotationChange(relativeDirection);
    }

    @Override
    public void publishHeadRotationChange(RelativeDirection relativeDirection) {
        double rotationDegrees = RelativeDirectionUtils.toAngleDegrees(relativeDirection);
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
            isDirectionChangeNotificationRequired = false;
            std_msgs.String directionMessage = relativeDirectionChangePublisher.newMessage();
            directionMessage.setData(currentDirection.name());
            relativeDirectionChangePublisher.publish(directionMessage);
        }
    }

    @Override
    public void onStrategyStatusChange(String strategyName) {
        isIdle = DrivingStrategy.DRIVING_STRATEGY_IDLE.equals(strategyName);
        publishHeadRotationChange(RelativeDirection.AHEAD);
    }
}
