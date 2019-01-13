package com.github.topnav_rosjava_kasptom.services;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import topnav_msgs.GuidelineMsg;

public class NavigationNode extends AbstractNodeMain {
    private Publisher<GuidelineMsg> guidelinePublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/navigator");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        guidelinePublisher = connectedNode.newPublisher("topnav/guidelines", GuidelineMsg._TYPE);
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
    }


    public Publisher<GuidelineMsg> getGuidelinePublisher() {
        return guidelinePublisher;
    }
}
