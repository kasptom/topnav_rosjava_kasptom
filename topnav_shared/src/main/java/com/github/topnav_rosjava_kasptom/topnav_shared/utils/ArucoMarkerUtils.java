package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection.EMPTY_DETECTION_ID;

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

    public static double distanceTo(MarkerDetection detection) {
        return EMPTY_DETECTION_ID.equals(detection.getId())
                ? Double.POSITIVE_INFINITY
                : Math.sqrt(Math.pow(detection.getCameraPosition()[0], 2) + Math.pow(detection.getCameraPosition()[2], 2));
    }
}
