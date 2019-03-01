package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import topnav_msgs.MarkersMsg;

import java.util.LinkedHashSet;

public interface IArUcoHeadTracker {
    void handleArUcoMessage(MarkersMsg markersMsg);

    void start();

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

    interface AngleCorrectionListener {
        void onAngleCorrection(double angleDegrees);
    }
}
