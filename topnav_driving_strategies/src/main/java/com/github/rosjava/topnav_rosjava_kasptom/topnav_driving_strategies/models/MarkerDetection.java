package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models;

public class MarkerDetection {
    String id;
    RelativePosition position;
    RelativeDirection direction;
}

enum RelativePosition {
    TOO_CLOSE, TOO_FAR, AT_PLACE, AT_MARKER_LEFT, AT_MARKER_RIGHT
}

enum RelativeDirection {
    AHEAD, AT_RIGHT, BEHIND, AT_LEFT, UNDEFINED
}
