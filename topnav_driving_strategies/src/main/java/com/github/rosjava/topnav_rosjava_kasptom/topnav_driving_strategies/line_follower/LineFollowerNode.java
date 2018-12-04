package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.line_follower;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import topnav_msgs.AngleRangesMsg;


@SuppressWarnings("unused")
public class LineFollowerNode extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/driving_strategies/line_follower");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<AngleRangesMsg> angleRangesMsgSubscriber = connectedNode.newSubscriber("capo/laser/angle_range", AngleRangesMsg._TYPE);
        angleRangesMsgSubscriber.addMessageListener(
                new WheelsController(connectedNode)
        );
    }
}
