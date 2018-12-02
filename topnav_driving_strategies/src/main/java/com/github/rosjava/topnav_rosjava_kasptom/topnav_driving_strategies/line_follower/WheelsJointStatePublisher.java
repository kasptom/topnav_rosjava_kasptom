package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.line_follower;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import sensor_msgs.JointState;
import topnav_msgs.AngleRangesMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WheelsJointStatePublisher implements MessageListener<AngleRangesMsg> {
    private final Publisher<JointState> wheelsStatePublisher;
    private Log log;
    private static final List<String> WHEEL_JOINT_NAMES = new ArrayList<>(Arrays.asList("front_left_wheel_joint", "rear_right_wheel_joint", "rear_left_wheel_joint", "front_right_wheel_joint"));

    WheelsJointStatePublisher(String jointStateTopicName, ConnectedNode connectedNode) {
        log = connectedNode.getLog();
        wheelsStatePublisher = connectedNode.newPublisher(jointStateTopicName, JointState._TYPE);
    }

    @Override
    public void onNewMessage(AngleRangesMsg angleRangesMsg) {
        double distance = angleRangesMsg.getDistances()[angleRangesMsg.getDistances().length / 2 + 1];
        log.info(String.format("Number of rays: %d", angleRangesMsg.getAngles().length));
        log.info(String.format("Distance to the front wall [m]: %.2f", distance));
        log.info(String.format("publishing on: %s", wheelsStatePublisher.getTopicName()));
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
        JointState message = wheelsStatePublisher.newMessage();
        message.setName(WHEEL_JOINT_NAMES);
        message.setVelocity(velocities);
        wheelsStatePublisher.publish(message);
    }
}
