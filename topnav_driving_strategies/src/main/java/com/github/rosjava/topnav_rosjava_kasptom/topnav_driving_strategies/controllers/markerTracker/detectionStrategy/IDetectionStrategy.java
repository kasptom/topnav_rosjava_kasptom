package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.detectionStrategy;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;

import java.util.List;

public interface IDetectionStrategy {
    MarkerDetection execute(List<MarkerDetection> detections);
}
