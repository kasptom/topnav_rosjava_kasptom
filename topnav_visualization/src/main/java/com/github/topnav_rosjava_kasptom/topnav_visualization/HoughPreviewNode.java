package com.github.topnav_rosjava_kasptom.topnav_visualization;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import sensor_msgs.LaserScan;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.GAZEBO_LASER_SCAN_TOPIC;

@SuppressWarnings("unused")
public class HoughPreviewNode extends AbstractNodeMain {

    private Subscriber<LaserScan> hokuyoSubscriber;
    private IHoughPreview preview;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("laser_scan_java_preview");


    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        preview = new HoughPreviewV2(log);

        hokuyoSubscriber = connectedNode.newSubscriber(GAZEBO_LASER_SCAN_TOPIC, LaserScan._TYPE);
        hokuyoSubscriber.addMessageListener(message -> preview.onLaserPointsUpdated(message));
    }
}
