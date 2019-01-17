package com.github.topnav_rosjava_kasptom.services;

import org.ros.message.MessageListener;
import topnav_msgs.FeedbackMsg;

public interface INavigationNode {
    void setFeedbackMessageListener(MessageListener<FeedbackMsg> feedbackMsgMessageListener);
}
