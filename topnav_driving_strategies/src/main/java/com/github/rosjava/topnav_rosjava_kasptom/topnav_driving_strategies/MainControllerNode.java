package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IHeadController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IMainController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.MainController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation.MarkerMessageHandler;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames;
import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.MarkersMsg;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.TOPNAV_ARUCO_TOPIC;


@SuppressWarnings("unused")
public class MainControllerNode extends AbstractNodeMain {

    private IMainController wheelsController;

    private Subscriber<MarkersMsg> markersMsgSubscriber;
    private Subscriber<std_msgs.String> strategyChangeSubscriber;

    private Publisher<FeedbackMsg> feedbackPublisher;
    private IHeadController headController;
    private MarkerMessageHandler markerMessageListener;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(TopicNames.TOPNAV_MAIN_CONTROLLER_NODE_NAME);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();

        wheelsController = new MainController(connectedNode);

        feedbackPublisher = connectedNode.newPublisher(TOPNAV_FEEDBACK_TOPIC, FeedbackMsg._TYPE);
        markerMessageListener = new MarkerMessageHandler(feedbackPublisher);

        markersMsgSubscriber = connectedNode.newSubscriber(TOPNAV_ARUCO_TOPIC, MarkersMsg._TYPE);
        strategyChangeSubscriber = connectedNode.newSubscriber(TOPNAV_STRATEGY_CHANGE_TOPIC, std_msgs.String._TYPE);

        markersMsgSubscriber.addMessageListener(markerMessageListener);
        strategyChangeSubscriber.addMessageListener(strategyName -> markerMessageListener.setCurrentStrategyName(strategyName.getData()));
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
