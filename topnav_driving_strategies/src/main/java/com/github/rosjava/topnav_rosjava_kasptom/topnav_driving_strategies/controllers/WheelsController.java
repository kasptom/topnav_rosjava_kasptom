package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class WheelsController implements WheelsVelocitiesChangeListener {
    private final LinkedHashMap<String, Publisher<Float64>> wheelPublishersMap;

    private Log log;
    private static final List<String> WHEEL_JOINT_NAMES = new ArrayList<>(Arrays.asList(
            "/capo_front_left_wheel_controller/command",
            "/capo_front_right_wheel_controller/command",
            "/capo_rear_left_wheel_controller/command",
            "/capo_rear_right_wheel_controller/command"));

    WheelsController(IDrivingStrategy drivingStrategy, ConnectedNode connectedNode) {
        HoughMessageHandler houghMessageHandler = new HoughMessageHandler(drivingStrategy);
        AngleRangeMessageHandler angleRangeMessageHandler = new AngleRangeMessageHandler(drivingStrategy);

        Subscriber<AngleRangesMsg> angleRangesMsgSubscriber = connectedNode.newSubscriber("capo/laser/angle_range", AngleRangesMsg._TYPE);
        Subscriber<HoughAcc> houghAccSubscriber = connectedNode.newSubscriber("capo/laser/hough", HoughAcc._TYPE);

        drivingStrategy.setWheelsVelocitiesListener(this);

        houghAccSubscriber.addMessageListener(houghMessageHandler);
        angleRangesMsgSubscriber.addMessageListener(angleRangeMessageHandler);

        log = connectedNode.getLog();
        wheelPublishersMap = new LinkedHashMap<>();

        for (String topicName : WHEEL_JOINT_NAMES) {
            wheelPublishersMap.put(topicName, connectedNode.<Float64>newPublisher(topicName, Float64._TYPE));
        }
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

    @Override
    public void onWheelsVelocitiesChanged(WheelsVelocities velocities) {
        setVelocities(velocities);
    }

    class HoughMessageHandler implements MessageListener<HoughAcc> {

        private IDrivingStrategy drivingStrategy;

        HoughMessageHandler(IDrivingStrategy drivingStrategy) {
            this.drivingStrategy = drivingStrategy;
        }

        @Override
        public void onNewMessage(HoughAcc houghAcc) {
            this.drivingStrategy.handleHoughAccMessage(houghAcc);
        }
    }

    class AngleRangeMessageHandler implements MessageListener<AngleRangesMsg> {

        private IDrivingStrategy drivingStrategy;

        AngleRangeMessageHandler(IDrivingStrategy drivingStrategy) {
            this.drivingStrategy = drivingStrategy;
        }

        @Override
        public void onNewMessage(AngleRangesMsg angleRangesMsg) {
            this.drivingStrategy.handleAngleRangeMessage(angleRangesMsg);
        }
    }

    public interface IDrivingStrategy {
        void handleHoughAccMessage(HoughAcc houghAcc);

        void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg);

        void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener);
    }
}
