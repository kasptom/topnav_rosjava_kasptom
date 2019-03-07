package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.detectionStrategy;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class ChooseClosestDetectionStrategy implements IDetectionStrategy {

    private final LinkedHashSet<String> trackedMarkerIds;

    public ChooseClosestDetectionStrategy(LinkedHashSet<String> trackedMarkerIds) {
        this.trackedMarkerIds = trackedMarkerIds;
    }

    @Override
    public MarkerDetection execute(List<MarkerDetection> detections) {
        return detections.stream()
                .filter(detection -> trackedMarkerIds.contains(detection.getId()))
                .min(Comparator.comparingDouble(ArucoMarkerUtils::distanceTo))
                .orElse(MarkerDetection.emptyDetection());
    }
}
