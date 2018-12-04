package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

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

    public WheelsController(ConnectedNode connectedNode) {
        log = connectedNode.getLog();
        wheelPublishersMap = new LinkedHashMap<>();

        for (String topicName : WHEEL_JOINT_NAMES) {
            wheelPublishersMap.put(topicName, connectedNode.<Float64>newPublisher(topicName ,Float64._TYPE));
        }
    }

    @Override
    public void onNewMessage(AngleRangesMsg angleRangesMsg) {
        double distance = angleRangesMsg.getDistances()[angleRangesMsg.getDistances().length / 2 + 1];
        log.info(String.format("Number of rays: %d", angleRangesMsg.getAngles().length));
        log.info(String.format("Distance to the front wall [m]: %.2f", distance));
        if (distance <= 1.0) {
            setVelocities(0.0, 0.0, 0.0, 0.0);
        } else {
            setVelocities(2.0, 2.0, 2.0, 2.0);
        }
    }

    /**
     * @param frontLeft  - front_left_wheel_joint
     * @param frontRight - rear_right_wheel_joint
     * @param rearLeft   - rear_left_wheel_joint
     * @param rearRight  - front_right_wheel_joint
     */
    private void setVelocities(double frontLeft, double frontRight, double rearLeft, double rearRight) {
        log.info(String.format("Setting velocities (%.2f, %.2f,%.2f, %.2f)", frontLeft, frontRight, rearLeft, rearRight));
        double[] velocities = new double[]{frontLeft, frontRight, rearLeft, rearRight};


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
}
