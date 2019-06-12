package com.github.topnav_rosjava_kasptom.services;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.GuidelineMsg;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.NodeNames.TOPNAV_NAVIGATOR_NODE_NAME;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.*;

public class NavigationNode extends AbstractNodeMain implements INavigationNode {
    private Publisher<GuidelineMsg> guidelinePublisher;
    private Publisher<std_msgs.String> cameraDirectionPublisher;
    private Publisher<std_msgs.Int16> steeringCommandPublisher;
    private Subscriber<FeedbackMsg> feedbackSubscriber;
    private MessageListener<FeedbackMsg> feedbackMessageListener;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(TOPNAV_NAVIGATOR_NODE_NAME);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        guidelinePublisher = connectedNode.newPublisher(TOPNAV_GUIDELINES_TOPIC, GuidelineMsg._TYPE);
        cameraDirectionPublisher = connectedNode.newPublisher(TOPNAV_NAVIGATION_HEAD_DIRECTION_TOPIC, std_msgs.String._TYPE);
        steeringCommandPublisher = connectedNode.newPublisher(TOPNAV_NAVIGATION_MANUAL_STEERING_TOPIC, std_msgs.Int16._TYPE);

        feedbackSubscriber = connectedNode.newSubscriber(TOPNAV_FEEDBACK_TOPIC, FeedbackMsg._TYPE);
        feedbackSubscriber.addMessageListener(feedbackMessageListener);
    }

    @Override
    public void setFeedbackMessageListener(MessageListener<FeedbackMsg> feedbackMsgMessageListener) {
        this.feedbackMessageListener = feedbackMsgMessageListener;
    }

    Publisher<GuidelineMsg> getGuidelinePublisher() {
        return guidelinePublisher;
    }

    Publisher<std_msgs.String> getCameraDirectionPublisher() {
        return cameraDirectionPublisher;
    }

    Publisher<std_msgs.Int16> getSteeringCommandPublisher() {
        return steeringCommandPublisher;
    }
}
