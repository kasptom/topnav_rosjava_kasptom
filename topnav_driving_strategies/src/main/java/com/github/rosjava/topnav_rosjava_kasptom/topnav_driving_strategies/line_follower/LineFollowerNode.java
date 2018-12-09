package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.line_follower;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import models.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import topnav_msgs.AngleRangesMsg;


@SuppressWarnings("unused")
public class LineFollowerNode extends AbstractNodeMain {

    private WheelsController wheelsController;
    private Log log;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/driving_strategies/line_follower");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        log = connectedNode.getLog();
        Subscriber<AngleRangesMsg> angleRangesMsgSubscriber = connectedNode.newSubscriber("capo/laser/angle_range", AngleRangesMsg._TYPE);
        wheelsController = new WheelsController(connectedNode, new WheelsController.AngleRangeMessageHandler() {
            @Override
            public WheelsVelocities handleMessage(AngleRangesMsg message) {

                double distance = message.getDistances()[message.getDistances().length / 2 + 1];
                log.info(String.format("Number of rays: %d", message.getAngles().length));
                log.info(String.format("Distance to the front wall [m]: %.2f", distance));

                return distance > 1.0 ?
                        new WheelsVelocities(2.0, 2.0, 2.0, 2.0) :
                        new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
            }
        });
        angleRangesMsgSubscriber.addMessageListener(wheelsController);
    }
}
