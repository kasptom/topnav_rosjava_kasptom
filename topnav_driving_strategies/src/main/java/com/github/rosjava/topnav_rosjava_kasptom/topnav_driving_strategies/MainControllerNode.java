package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation.MarkerMessageHandler;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;
import topnav_msgs.MarkersMsg;

//import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.HoughLineTestStrategy;
//import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.StopBeforeWallStrategy;


@SuppressWarnings("unused")
public class MainControllerNode extends AbstractNodeMain {

    private WheelsController wheelsController;
    private Subscriber<MarkersMsg> markersMsgSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/driving_strategies/line_follower");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        IDrivingStrategy drivingStrategy = new DriveAlongWallStrategy(log);
        wheelsController = new WheelsController(drivingStrategy, connectedNode);
        markersMsgSubscriber = connectedNode.newSubscriber("capo/camera/aruco", MarkersMsg._TYPE);
        markersMsgSubscriber.addMessageListener(new MarkerMessageHandler());
    }

    @Override
    public void onShutdown(Node node) {
        if (wheelsController != null) {
            wheelsController.emergencyStop();
        }

        if (markersMsgSubscriber != null) {
            markersMsgSubscriber.removeAllMessageListeners();
        }
        super.onShutdown(node);
    }
}
