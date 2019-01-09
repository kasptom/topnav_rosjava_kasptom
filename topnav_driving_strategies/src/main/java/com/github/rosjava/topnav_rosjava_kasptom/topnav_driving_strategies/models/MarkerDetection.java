package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models;

public class MarkerDetection {
    private String id;
    private final double[] cameraPosition = new double[3];

    public static MarkerDetection createDetection(String id, double[] cameraPosition) {
        return new MarkerDetection(id, cameraPosition);
    }

    public double[] getCameraPosition() {
        return cameraPosition;
    }

    public String getId() {
        return id;
    }

    public RelativeAlignment getRelativeAlignment() {
        if (cameraPosition[0] < -0.5) {
            return RelativeAlignment.RIGHT;
        } else if (cameraPosition[0] > 0.5) {
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

    private MarkerDetection(String id, double[] cameraPosition) {
        this.id = id;
        System.arraycopy(cameraPosition, 0, this.cameraPosition, 0, cameraPosition.length);
    }
}

enum RelativeDirection {
    AHEAD, AT_RIGHT, BEHIND, AT_LEFT, UNDEFINED
}
