package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
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
