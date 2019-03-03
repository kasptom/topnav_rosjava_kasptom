package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import topnav_msgs.MarkersMsg;

import java.util.LinkedHashSet;

public interface IArUcoHeadTracker {
    void handleArUcoMessage(MarkersMsg markersMsg);

    void start(double initialAngleDegrees);

    void stop();

    /**
     * Sets the markers to track
     *
     * @param markerIds ids of markers given in the priority of tracking. The earlier the marker is on the list
     *                  the higher priority of being tracked it has.
     *                  Set null the
     */
    void setTrackedMarkers(LinkedHashSet<String> markerIds);

    void setAngleCorrectionListener(AngleCorrectionListener listener);

    void setTrackedMarkerListener(TrackedMarkerListener listener);

    interface AngleCorrectionListener {
        void onAngleCorrection(double angleDegrees);
    }

    interface TrackedMarkerListener {
        void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation);
    }
}
