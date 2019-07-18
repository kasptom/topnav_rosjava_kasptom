package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadLinearRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.detectionStrategy.ChooseClosestDetectionStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.detectionStrategy.IDetectionStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import org.apache.commons.logging.Log;
import std_msgs.Float64;
import std_msgs.UInt64;
import topnav_msgs.MarkersMsg;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_FOV_DEGREES;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_PREVIEW_WIDTH;

public class ArUcoHeadTracker implements IArUcoHeadTracker {
    private static final double UNDEFINED_ANGLE = -1000.0;

    private final LinkedHashSet<String> trackedMarkerIds;
    private final LinkedHashSet<MarkerDetection> foundMarkers;
    private final Log log;
    private IDetectionStrategy detectionStrategy;

    private double currentSearchAngle;
    private boolean isEnabled;
    private boolean isLookingForMarkers;
    private boolean isHeadRotationInProgress;
    private boolean minTimeSinceLastRotationElapsed;

    private HeadLinearRotationChangeRequestListener headRotationChangeListener;
    private TrackedMarkerListener trackedMarkerListener;
    private double angleDegrees;

    public ArUcoHeadTracker(Log log) {
        currentSearchAngle = MIN_SEARCH_ANGLE;
        this.trackedMarkerIds = new LinkedHashSet<>();
        this.foundMarkers = new LinkedHashSet<>();
        this.log = log;
    }

    @Override
    public void handleArUcoMessage(MarkersMsg markersMsg) {
        if (!isEnabled) {
            return;
        }

        if (isLookingForMarkers) {
            lookAroundForMarkers(markersMsg);
            return;
        }

        trackMarker(markersMsg);
    }

    @Override
    public void handleHeadRotationChange(Float64 headRotationMessage) {
        currentSearchAngle = headRotationMessage.getData();
        isHeadRotationInProgress = false;
    }

    @Override
    public void handleTimeSinceLastRotationMessage(UInt64 timeSinceLastRotationMessage) {
        minTimeSinceLastRotationElapsed = timeSinceLastRotationMessage.getData() > CAMERA_LATENCY_MS;
    }

    @Override
    public void start() {
        start(MIN_SEARCH_ANGLE, true);
    }

    @Override
    public void start(double initialAngleDegrees, boolean isLookingForMarkers) {
        foundMarkers.clear();
        angleDegrees = UNDEFINED_ANGLE;
        currentSearchAngle = MIN_SEARCH_ANGLE;

        detectionStrategy = new ChooseClosestDetectionStrategy(trackedMarkerIds);
        isHeadRotationInProgress = true;
        minTimeSinceLastRotationElapsed = false;
        headRotationChangeListener.onLinearRotationRequestChange(currentSearchAngle);

        this.isLookingForMarkers = isLookingForMarkers;
        isEnabled = true;
    }

    @Override
    public void stop() {
        isEnabled = false;
    }

    @Override
    public void setTrackedMarkers(LinkedHashSet<String> markerIds) {
        trackedMarkerIds.clear();
        if (markerIds == null || markerIds.isEmpty()) {
            return;
        }

        trackedMarkerIds.addAll(markerIds);
    }

    @Override
    public void setAngleChangeListener(HeadLinearRotationChangeRequestListener listener) {
        headRotationChangeListener = listener;
    }

    @Override
    public void setTrackedMarkerListener(TrackedMarkerListener listener) {
        trackedMarkerListener = listener;
    }

    private void lookAroundForMarkers(MarkersMsg markersMsg) {
        if (isHeadRotationInProgress || !minTimeSinceLastRotationElapsed) {
            return;
        }

        List<MarkerDetection> detections = ArucoMarkerUtils.createMarkerDetections(markersMsg);
        detections.stream()
                .filter(detection -> trackedMarkerIds.contains(detection.getId())
                        && foundMarkers.stream().noneMatch(found -> found.getId().equals(detection.getId())))
                .forEach(detection -> {
                    foundMarkers.add(detection);
                    log.debug(String.format("detected: %s at angle %.2f", detection.getId(), currentSearchAngle));
                    detection.setDetectedAtAngle(currentSearchAngle);
                });

        if (currentSearchAngle == MAX_SEARCH_ANGLE) {
            if (foundMarkers.isEmpty()) {
                isEnabled = false;
                log.info("Could not find any of the listed markers in the surroundings");
                trackedMarkerListener.onTrackedMarkerUpdate(MarkerDetection.emptyDetection(), RelativeDirection.AHEAD.getRotationDegrees());
            } else {
                log.info(String.format("Found %d/%d markers", foundMarkers.size(), trackedMarkerIds.size()));
                MarkerDetection firstDetection = detectionStrategy.execute(new ArrayList<>(foundMarkers));
                double angle = firstDetection == null ? 0 : firstDetection.getDetectedAtAngle();
                angleDegrees = angle;
                isHeadRotationInProgress = true;
                headRotationChangeListener.onLinearRotationRequestChange(angle);
            }
            isLookingForMarkers = false;
            return;
        }

        currentSearchAngle += SEARCH_ANGLE_STEP_DEGREES;
        log.debug(String.format("Looking for markers at angle: %.2f", currentSearchAngle));
        if (currentSearchAngle >= MAX_SEARCH_ANGLE) {
            currentSearchAngle = MAX_SEARCH_ANGLE;
        }

        headRotationChangeListener.onLinearRotationRequestChange(currentSearchAngle);
        isHeadRotationInProgress = true;
    }

    private void trackMarker(MarkersMsg markersMsg) {
        if (isHeadRotationInProgress || !minTimeSinceLastRotationElapsed) {
            return;
        }

        List<MarkerDetection> detections = ArucoMarkerUtils.createMarkerDetections(markersMsg);
        MarkerDetection trackedDetection = detectionStrategy.execute(detections);

        if (!isValidMarker(trackedDetection)) {
            trackedMarkerListener.onTrackedMarkerUpdate(MarkerDetection.emptyDetection(), 0.0);
            return; // TODO warn / error / handling
        }

        centerHeadOn(trackedDetection);
    }

    private boolean isValidMarker(MarkerDetection trackedDetection) {
        return trackedDetection != null && !trackedDetection.isEmptyDetection();
    }

    private void centerHeadOn(MarkerDetection marker) {
        double[] xCorners = marker.getXCorners();
        double averagePicturePosition = (xCorners[0] + xCorners[1] + xCorners[2] + xCorners[3]) / 4.0 - CAM_PREVIEW_WIDTH / 2.0;    // 0 is the middle of the picture
        double headRotationCorrection = -averagePicturePosition * CAM_FOV_DEGREES / CAM_PREVIEW_WIDTH;

        angleDegrees += headRotationCorrection;
        System.out.printf("setting angle to %.2f\n", angleDegrees);

        isHeadRotationInProgress = true;
        headRotationChangeListener.onLinearRotationRequestChange(angleDegrees);
        trackedMarkerListener.onTrackedMarkerUpdate(marker, angleDegrees);
    }

}
