package models;

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
}