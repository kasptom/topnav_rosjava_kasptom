package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public enum RelativeDirection {
    AHEAD(0.0), AT_RIGHT(-90.0), BEHIND(-180.0), AT_LEFT(90), UNDEFINED(0.0);

    private final double rotationDegrees;

    RelativeDirection(double rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public double getRotationDegrees() {
        return rotationDegrees;
    }
}
