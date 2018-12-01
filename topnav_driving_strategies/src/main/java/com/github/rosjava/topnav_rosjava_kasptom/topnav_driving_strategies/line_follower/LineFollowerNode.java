package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.line_follower;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import topnav_msgs.AngleRangesMsg;


public class LineFollowerNode extends AbstractNodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/driving_strategies/line_follower");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Log log = connectedNode.getLog();
        Subscriber<AngleRangesMsg> subscriber = connectedNode.newSubscriber("angle_range", AngleRangesMsg._TYPE);
    }
}
