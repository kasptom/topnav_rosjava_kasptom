package com.github.topnav_rosjava_kasptom.topnav_navigator;

import com.github.topnav_rosjava_kasptom.topnav_navigator.presenter.IGuidelinePresenter;
import com.github.topnav_rosjava_kasptom.topnav_navigator.presenter.GuidelinePresenter;
import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import topnav_msgs.GuidelineMsg;

public class NavigationNode extends AbstractNodeMain {

    private IGuidelinePresenter presenter = new GuidelinePresenter();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/navigator");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        Publisher<GuidelineMsg> guideLinePublisher = connectedNode.newPublisher("topnav/guidelines", GuidelineMsg._TYPE);
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
    }


}
