package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.detectionStrategy;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ChooseFirstInOrderDetectionStrategy implements IDetectionStrategy {

    private final LinkedHashSet<String> trackedMarkerIds;

    public ChooseFirstInOrderDetectionStrategy(LinkedHashSet<String> trackedMarkerIds) {
        this.trackedMarkerIds = trackedMarkerIds;
    }

    @Override
    public MarkerDetection execute(List<MarkerDetection> detections) {
        MarkerDetection trackedDetection;
        List<String> detectedMarkerIds = detections
                .stream()
                .map(MarkerDetection::getId)
                .collect(Collectors.toList());

        final String trackedMarkerId = trackedMarkerIds
                .stream()
                .filter(detectedMarkerIds::contains)
                .findFirst()
                .orElse(null);

        if (trackedMarkerId == null) return null;

        trackedDetection =
                detections.stream()
                        .filter(detection -> detection.getId().equals(trackedMarkerId))
                        .findFirst()
                        .orElse(null);

        return trackedDetection;
    }
}
