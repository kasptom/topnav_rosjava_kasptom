package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.FeedbackUtils;
import org.ros.message.MessageListener;
import org.ros.node.topic.Publisher;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.HashMap;

public class MarkerMessageHandler implements MessageListener<MarkersMsg> {
    private final Publisher<FeedbackMsg> feedbackPublisher;
    private HashMap<String, Long> previousTimeStamp = new HashMap<>();
    private String strategyName = DrivingStrategy.DRIVING_STRATEGY_IDLE;

    public MarkerMessageHandler(Publisher<FeedbackMsg> feedbackPublisher) {
        this.feedbackPublisher = feedbackPublisher;
    }

    @Override
    public void onNewMessage(MarkersMsg markersMsg) {
        long currentTimeStamp = System.nanoTime();
        FeedbackMsg feedbackMsg = feedbackPublisher.newMessage();
        FeedbackUtils.fillInFeedbackMsg(feedbackMsg, markersMsg, currentTimeStamp, strategyName);
        feedbackPublisher.publish(feedbackMsg);
//        markersMsg.getMarkers().forEach(this::printMarkerMessage);
    }

    public void setCurrentStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    private void printMarkerMessage(MarkerMsg markerMsg) {
        double[] pos = markerMsg.getCameraPosition();
        MarkerDetection detection = MarkerDetection.createDetection(Integer.toString(markerMsg.getId()), markerMsg.getCameraPosition(), markerMsg.getXCorners(), markerMsg.getYCorners());
        Long currentTimeStamp = System.nanoTime();

        if (!previousTimeStamp.containsKey(detection.getId())
                || currentTimeStamp - previousTimeStamp.get(detection.getId()) > 1e9) {
            previousTimeStamp.put(detection.getId(), currentTimeStamp);
            System.out.printf("Marker ID: %s\n Position: (%.2f, %.2f, %.2f)\n %s %s\n",
                    detection.getId(), pos[0], pos[1], pos[2], detection.getRelativeAlignment(), detection.getRelativeDistance());
        }
    }
}
