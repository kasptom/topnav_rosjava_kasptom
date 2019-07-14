package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

public class ManeuverDescription {

    private String name;
    private double rotationDegrees;
    private double distanceMeters;

    public ManeuverDescription(String name, double rotationDegrees, double distanceMeters) {
        this.name = name;
        this.rotationDegrees = rotationDegrees;
        this.distanceMeters = distanceMeters;
    }

    public String getName() {
        return name;
    }

    public double getRotationDegrees() {
        return rotationDegrees;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }
}
