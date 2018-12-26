package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
//import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.HoughLineTestStrategy;
//import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.StopBeforeWallStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.HoughLineTestStrategy;
import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;


@SuppressWarnings("unused")
public class MainControllerNode extends AbstractNodeMain {

    private WheelsController wheelsController;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/driving_strategies/line_follower");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        WheelsController.IDrivingStrategy drivingStrategy = new DriveAlongWallStrategy(log);
        wheelsController = new WheelsController(drivingStrategy, connectedNode);
    }

    @Override
    public void onShutdown(Node node) {
        wheelsController.emergencyStop();
        super.onShutdown(node);
    }
}
