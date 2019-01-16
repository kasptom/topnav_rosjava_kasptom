package com.github.topnav_rosjava_kasptom.services;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import std_msgs.Float64;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.GuidelineMsg;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.HEAD_JOINT_TOPIC;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.TOPNAV_FEEDBACK_TOPIC;

public class NavigationNode extends AbstractNodeMain implements INavigationNode {
    private Publisher<GuidelineMsg> guidelinePublisher;
    private Publisher<Float64> cameraDirectionPublisher;
    private Subscriber<FeedbackMsg> feedbackSubscriber;
    private MessageListener<FeedbackMsg> feedbackMessageListener;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("topnav/navigator");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log log = connectedNode.getLog();
        guidelinePublisher = connectedNode.newPublisher("topnav/guidelines", GuidelineMsg._TYPE);
        cameraDirectionPublisher = connectedNode.newPublisher(HEAD_JOINT_TOPIC, Float64._TYPE);

        feedbackSubscriber = connectedNode.newSubscriber(TOPNAV_FEEDBACK_TOPIC, FeedbackMsg._TYPE);
        feedbackSubscriber.addMessageListener(feedbackMessageListener);
    }

    @Override
    public void setFeedbackMessageListener(MessageListener<FeedbackMsg> feedbackMsgMessageListener) {
        this.feedbackMessageListener = feedbackMsgMessageListener;
    }

    public Publisher<GuidelineMsg> getGuidelinePublisher() {
        return guidelinePublisher;
    }

    public Publisher<Float64> getCameraDirectionPublisher() {
        return cameraDirectionPublisher;
    }
}
