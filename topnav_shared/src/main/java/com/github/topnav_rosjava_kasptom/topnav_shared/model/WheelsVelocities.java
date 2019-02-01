package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class WheelsVelocities {
    private double frontLeft;
    private double frontRight;
    private double rearLeft;
    private double rearRight;

    public WheelsVelocities(double frontLeft, double frontRight, double rearLeft, double rearRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
    }

    public double getFrontLeft() {
        return frontLeft;
    }

    public double getFrontRight() {
        return frontRight;
    }

    public double getRearLeft() {
        return rearLeft;
    }

    public double getRearRight() {
        return rearRight;
    }

    public static WheelsVelocities addVelocities(WheelsVelocities firstVelocity, WheelsVelocities secondVelocity) {
        double frontLeft = firstVelocity.getFrontLeft() + secondVelocity.getFrontLeft();
        double frontRight = firstVelocity.getFrontRight() + secondVelocity.getFrontRight();
        double rearLeft = firstVelocity.getRearLeft() + secondVelocity.getRearLeft();
        double rearRight = firstVelocity.getRearRight() + secondVelocity.getRearRight();
        return new WheelsVelocities(frontLeft, frontRight, rearLeft, rearRight);
    }
}