package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import std_msgs.Float64;
import topnav_msgs.AngleRangesMsg;

import java.util.*;

public class WheelsController implements MessageListener<AngleRangesMsg> {
    private final LinkedHashMap<String, Publisher<Float64>> wheelPublishersMap;
    private Log log;
    private static final List<String> WHEEL_JOINT_NAMES = new ArrayList<>(Arrays.asList(
            "/capo_front_left_wheel_controller/command",
            "/capo_front_right_wheel_controller/command",
            "/capo_rear_left_wheel_controller/command",
            "/capo_rear_right_wheel_controller/command"));

    private AngleRangeMessageHandler angleRangeMsgHandler;

    public WheelsController(ConnectedNode connectedNode, AngleRangeMessageHandler handler) {
        log = connectedNode.getLog();
        wheelPublishersMap = new LinkedHashMap<>();
        this.angleRangeMsgHandler = handler;

        for (String topicName : WHEEL_JOINT_NAMES) {
            wheelPublishersMap.put(topicName, connectedNode.<Float64>newPublisher(topicName, Float64._TYPE));
        }
    }

    @Override
    public void onNewMessage(AngleRangesMsg angleRangesMsg) {

        if (angleRangeMsgHandler == null) {
            log.warn("Set AngleRangeMessageHandler");
            return;
        }

        log.info(String.format("new message: %s", angleRangesMsg.toString()));

        WheelsVelocities velocities = angleRangeMsgHandler.handleMessage(angleRangesMsg);
        setVelocities(velocities);
    }

    private void setVelocities(WheelsVelocities wheelsVelocities) {
        double[] velocities = new double[]{
                wheelsVelocities.getFrontLeft(),
                wheelsVelocities.getFrontRight(),
                wheelsVelocities.getRearLeft(),
                wheelsVelocities.getRearRight()
        };
        log.info(String.format("Setting velocities (%.2f, %.2f,%.2f, %.2f)",
                velocities[0], velocities[1], velocities[2], velocities[3]));

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

    public interface AngleRangeMessageHandler {
        WheelsVelocities handleMessage(AngleRangesMsg message);
    }
}
