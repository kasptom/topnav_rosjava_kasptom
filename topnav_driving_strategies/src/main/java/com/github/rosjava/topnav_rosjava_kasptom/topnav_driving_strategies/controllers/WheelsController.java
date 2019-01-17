package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import std_msgs.Float64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class WheelsController implements IWheelsController {
    private final Log log;

    private LinkedHashMap<String, Publisher<Float64>> wheelPublishersMap;

    private static final List<String> WHEEL_JOINT_NAMES = new ArrayList<>(Arrays.asList(
            "/capo_front_left_wheel_controller/command",
            "/capo_front_right_wheel_controller/command",
            "/capo_rear_left_wheel_controller/command",
            "/capo_rear_right_wheel_controller/command"));

    private WheelsVelocities currentVelocity = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);


    WheelsController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();
        setUpJointsPublishers(connectedNode);
    }

    @Override
    public void setVelocities(WheelsVelocities wheelsVelocities) {
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

    private void setUpJointsPublishers(ConnectedNode connectedNode) {
        wheelPublishersMap = new LinkedHashMap<>();
        for (String topicName : WHEEL_JOINT_NAMES) {
            wheelPublishersMap.put(topicName, connectedNode.newPublisher(topicName, Float64._TYPE));
        }
    }
}
