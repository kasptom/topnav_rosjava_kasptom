package com.github.topnav_rosjava_kasptom.topnav_visualization;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import topnav_msgs.AngleRangesMsg;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.TOPNAV_ANGLE_RANGE_TOPIC;

@SuppressWarnings("unused")
public class HoughPreviewNode extends AbstractNodeMain {

    private Subscriber<AngleRangesMsg> angleRangeSubscriber;
    private IHoughPreview preview;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("laser_scan_java_preview");


    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        preview = new HoughPreviewV2(log);

        angleRangeSubscriber = connectedNode.newSubscriber(TOPNAV_ANGLE_RANGE_TOPIC, AngleRangesMsg._TYPE);
        angleRangeSubscriber.addMessageListener(message -> preview.onAngleRangeMessage(message));
    }
}
