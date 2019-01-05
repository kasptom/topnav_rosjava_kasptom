package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models;

public class MarkerDetection {
    String id;
    RelativePosition position;
    RelativeDirection direction;
    private final double[] cameraPosition = new double[3];

    public static MarkerDetection createDetection(String id, double[] cameraPosition) {
        return new MarkerDetection(id, cameraPosition);
    }

    public double[] getCameraPosition() {
        return cameraPosition;
    }

    private MarkerDetection(String id, double[] cameraPosition) {
        this.id = id;
        System.arraycopy(cameraPosition, 0, this.cameraPosition, 0, cameraPosition.length);
    }
}



enum RelativePosition {
    TOO_CLOSE, TOO_FAR, AT_PLACE, AT_MARKER_LEFT, AT_MARKER_RIGHT
}

enum RelativeDirection {
    AHEAD, AT_RIGHT, BEHIND, AT_LEFT, UNDEFINED
}
