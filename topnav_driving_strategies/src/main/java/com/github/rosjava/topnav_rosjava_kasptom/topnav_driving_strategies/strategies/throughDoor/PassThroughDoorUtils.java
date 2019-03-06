package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.TopologyMsg;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.*;

public class PassThroughDoorUtils {
    public static List<TopologyMsg> findFrontDoorMarkers(FeedbackMsg message, HashMap<String, GuidelineParam> guidelineParamsMap) {
        return findDoorMarkers(message, KEY_FRONT_LEFT_MARKER_ID, KEY_FRONT_RIGHT_MARKER_ID, guidelineParamsMap);
    }

    public static List<TopologyMsg> findBackDoorMarkers(FeedbackMsg message, HashMap<String, GuidelineParam> guidelineParamsMap) {
        return findDoorMarkers(message, KEY_BACK_LEFT_MARKER_ID, KEY_BACK_RIGHT_MARKER_ID, guidelineParamsMap);
    }

    private static List<TopologyMsg> findDoorMarkers(FeedbackMsg feedbackMsg, String leftMarkerKey, String rightMarkerKey, HashMap<String, GuidelineParam> guidelineParamsMap) {
        String leftMarkerId = guidelineParamsMap.get(leftMarkerKey).getValue();
        String rightMarkerId = guidelineParamsMap.get(rightMarkerKey).getValue();

        return feedbackMsg.getTopologies()
                .stream()
                .filter(topologyMsg -> topologyMsg.getIdentity().equals(leftMarkerId)
                        || topologyMsg.getIdentity().equals(rightMarkerId))
                .collect(Collectors.toList());
    }
}
