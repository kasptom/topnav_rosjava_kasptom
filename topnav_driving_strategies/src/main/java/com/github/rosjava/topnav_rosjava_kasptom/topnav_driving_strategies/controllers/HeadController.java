package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.RelativeDirectionUtils;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.*;

public class HeadController implements IHeadController {

    private final Log log;
    private final Subscriber<std_msgs.String> drivingHeadRotationSubscriber;
    private final Subscriber<std_msgs.String> navigationHeadRotationSubscriber;
    private boolean isIdle = true;
    private Publisher<Float64> headRotationPublisher;

    HeadController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();

        drivingHeadRotationSubscriber = connectedNode.newSubscriber(TOPNAV_STRATEGY_HEAD_DIRECTION_TOPIC, std_msgs.String._TYPE);
        navigationHeadRotationSubscriber = connectedNode.newSubscriber(TOPNAV_NAVIGATION_HEAD_DIRECTION_TOPIC, std_msgs.String._TYPE);

        drivingHeadRotationSubscriber.addMessageListener(message -> handleStrategyHeadRotationChange(RelativeDirectionUtils.convertMessageToRelativeDirection(message)));
        navigationHeadRotationSubscriber.addMessageListener(message -> handleNavigationHeadRotationChange(RelativeDirectionUtils.convertMessageToRelativeDirection(message)));

        headRotationPublisher = connectedNode.newPublisher(HEAD_JOINT_TOPIC, Float64._TYPE);
    }

    @Override
    public void handleStrategyHeadRotationChange(RelativeDirection relativeDirection) {
        publishHeadRotationChange(relativeDirection);
    }

    @Override
    public void handleNavigationHeadRotationChange(RelativeDirection relativeDirection) {
        if (!isIdle) {
            return;
        }
        publishHeadRotationChange(relativeDirection);
    }

    @Override
    public void publishHeadRotationChange(RelativeDirection relativeDirection) {
        double rotationDegrees = RelativeDirectionUtils.convertRelativeDirectionToAngleDegrees(relativeDirection);
        double rotationRads = rotationDegrees * Math.PI / 180.0;
        Float64 rotationMsg = headRotationPublisher.newMessage();
        rotationMsg.setData(rotationRads);
        headRotationPublisher.publish(rotationMsg);
    }

    @Override
    public void onStrategyStatusChange(String strategyName) {
        isIdle = DrivingStrategy.DRIVING_STRATEGY_IDLE.equals(strategyName);
    }
}
