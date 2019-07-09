package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

public class ManeuverDescription {
    public ManeuverDescription(String name, double rotationDegrees, double distanceMeters) {
        this.name = name;
        this.rotationDegrees = rotationDegrees;
        this.distanceMeters = distanceMeters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(double rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    private String name;
    private double rotationDegrees;
    private double distanceMeters;
}
