package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ArucoMarkerUtils {
    public static List<MarkerDetection> createMarkerDetections(MarkersMsg markersMsg) {
        return markersMsg.getMarkers()
                .stream()
                .map(ArucoMarkerUtils::createMarkerDetection)
                .collect(Collectors.toList());
    }

    private static MarkerDetection createMarkerDetection(MarkerMsg marker) {
        return MarkerDetection.createDetection(Integer.toString(marker.getId()), marker.getCameraPosition(), marker.getXCorners(), marker.getYCorners());
    }

    public static LinkedHashSet<String> asOrderedDoorMarkerIds(HashMap<String, GuidelineParam> guidelineParams) {
        LinkedHashSet<String> doorMarkersOrdered = new LinkedHashSet<>(4);
        doorMarkersOrdered.add(guidelineParams.get(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID).getValue());
        doorMarkersOrdered.add(guidelineParams.get(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID).getValue());
        doorMarkersOrdered.add(guidelineParams.get(DrivingStrategy.ThroughDoor.KEY_BACK_LEFT_MARKER_ID).getValue());
        doorMarkersOrdered.add(guidelineParams.get(DrivingStrategy.ThroughDoor.KEY_BACK_RIGHT_MARKER_ID).getValue());
        return doorMarkersOrdered;
    }
}
