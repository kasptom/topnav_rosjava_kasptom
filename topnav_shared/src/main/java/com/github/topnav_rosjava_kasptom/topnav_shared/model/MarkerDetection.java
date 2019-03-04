package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class MarkerDetection {
    private static final String EMPTY_DETECTION_ID = "-1";

    private String id;
    private final double[] cameraPosition = new double[3];
    private final double[] xCorners = new double[4];
    private final double[] yCorners = new double[4];
    private static final double OFFSET_MARKER_CENTER_METERS = 0.1; // TODO topnav_config

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

    public double[] getXCorners() {
        return xCorners;
    }

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
        if (cameraPosition[2] < 0.5) {
            return RelativeDistance.CLOSE;
        } else if (cameraPosition[2] > 1.5) {
            return RelativeDistance.FAR;
        }
        return RelativeDistance.MIDDLE;
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
}

