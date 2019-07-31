package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.OFFSET_MARKER_CENTER_METERS;

public class MarkerDetection {
    public static final String EMPTY_DETECTION_ID = "-1";

    private String id;
    private final double[] cameraPosition = new double[3];
    private final double[] xCorners = new double[4];
    private final double[] yCorners = new double[4];


    private double detectedAtAngle = 0.0;

    public static MarkerDetection createDetection(String id, double[] cameraPosition, double[] xCorners, double[] yCorners) {
        return new MarkerDetection(id, cameraPosition, xCorners, yCorners);
    }

    public static MarkerDetection emptyDetection() {
        return new MarkerDetection(EMPTY_DETECTION_ID);
    }

    public double[] getCameraPosition() {
        return cameraPosition;
    }

    public String getId() {
        return id;
    }

    /**
     * @return x coordinates of the detected marker (in pixels, clockwise order from the top left corner)
     */
    public double[] getXCorners() {
        return xCorners;
    }

    /**
     * @return y coordinates of the detected marker (in pixels, clockwise order from the top left corner)
     */
    public double[] getYCorners() {
        return yCorners;
    }

    public RelativeAlignment getRelativeAlignment() {
        if (cameraPosition[0] < -OFFSET_MARKER_CENTER_METERS) {
            return RelativeAlignment.RIGHT;
        } else if (cameraPosition[0] > OFFSET_MARKER_CENTER_METERS) {
            return RelativeAlignment.LEFT;
        }
        return RelativeAlignment.CENTER;
    }

    public RelativeDistance getRelativeDistance() {
        if (cameraPosition[2] < Limits.MIN_MID_RANGE) {
            return RelativeDistance.CLOSE;
        } else if (cameraPosition[2] > Limits.MAX_MID_RANGE) {
            return RelativeDistance.FAR;
        }
        return RelativeDistance.MIDDLE;
    }

    public void setDetectedAtAngle(double angleDegrees) {
        detectedAtAngle = angleDegrees;
    }

    public double getDetectedAtAngle() {
        return detectedAtAngle;
    }

    public boolean isEmptyDetection() {
        return EMPTY_DETECTION_ID.equals(id);
    }

    private MarkerDetection(String id, double[] cameraPosition, double[] xCorners, double[] yCorners) {
        this.id = id;
        System.arraycopy(cameraPosition, 0, this.cameraPosition, 0, this.cameraPosition.length);
        System.arraycopy(xCorners, 0, this.xCorners, 0, this.xCorners.length);
        System.arraycopy(yCorners, 0, this.yCorners, 0, this.yCorners.length);
    }

    private MarkerDetection(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s [%.2f, %.2f, %.2f], %.2fm, %.2f°",
                id,
                cameraPosition[0], cameraPosition[1], cameraPosition[2],
                ArucoMarkerUtils.distanceTo(this), detectedAtAngle);
    }
}

