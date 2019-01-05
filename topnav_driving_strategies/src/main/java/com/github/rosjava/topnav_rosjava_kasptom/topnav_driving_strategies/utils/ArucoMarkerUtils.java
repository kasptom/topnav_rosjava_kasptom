package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.MarkerDetection;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.List;
import java.util.stream.Collectors;

public class ArucoMarkerUtils {
    static List<MarkerDetection> createMarkerDetections(MarkersMsg markersMsg) {
        return markersMsg.getMarkers()
                .stream()
                .map(ArucoMarkerUtils::createMarkerDetection)
                .collect(Collectors.toList());
    }

    private static MarkerDetection createMarkerDetection(MarkerMsg marker) {
        return MarkerDetection.createDetection(Integer.toString(marker.getId()), marker.getCameraPosition());
    }
}
