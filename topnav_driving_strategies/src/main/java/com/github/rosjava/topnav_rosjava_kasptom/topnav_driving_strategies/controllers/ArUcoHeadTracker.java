package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import topnav_msgs.MarkersMsg;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_FOV_DEGREES;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_PREVIEW_WIDTH;

public class ArUcoHeadTracker implements IArUcoHeadTracker {
    private final LinkedHashSet<String> trackedMarkerIds;
    private boolean isEnabled;
    private AngleCorrectionListener listener;
    private double angleDegrees;

    ArUcoHeadTracker() {
        this.trackedMarkerIds = new LinkedHashSet<>();
    }

    @Override
    public void handleArUcoMessage(MarkersMsg markersMsg) {
        if (!isEnabled) {
            return;
        }
        trackDoorMarker(markersMsg);
    }

    private void trackDoorMarker(MarkersMsg markersMsg) {
        List<MarkerDetection> detections = ArucoMarkerUtils.createMarkerDetections(markersMsg);
        MarkerDetection trackedDetection = getMarkerDetection(detections);

        if (trackedDetection == null) return; // TODO warn / error / handling

        centerHeadOn(trackedDetection);
    }

    private MarkerDetection getMarkerDetection(List<MarkerDetection> detections) {
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

    private void centerHeadOn(MarkerDetection marker) {
        double[] xCorners = marker.getXCorners();
        double averagePicturePosition = (xCorners[0] + xCorners[1] + xCorners[2] + xCorners[3]) / 4.0 - CAM_PREVIEW_WIDTH / 2.0;    // 0 is the middle of the picture
        double headRotationCorrection = -averagePicturePosition * CAM_FOV_DEGREES / CAM_PREVIEW_WIDTH;

        angleDegrees += headRotationCorrection;
        listener.onAngleCorrection(angleDegrees);
    }

    @Override
    public void start(double initialAngleDegrees) {
        angleDegrees = initialAngleDegrees;
        isEnabled = true;
    }

    @Override
    public void stop() {
        isEnabled = false;
    }

    @Override
    public void setTrackedMarkers(LinkedHashSet<String> markerIds) {
        this.trackedMarkerIds.clear();
        if (markerIds == null || markerIds.isEmpty()) {
            return;
        }

        this.trackedMarkerIds.addAll(markerIds);
    }

    @Override
    public void setAngleCorrectionListener(AngleCorrectionListener listener) {
        this.listener = listener;
    }
}
