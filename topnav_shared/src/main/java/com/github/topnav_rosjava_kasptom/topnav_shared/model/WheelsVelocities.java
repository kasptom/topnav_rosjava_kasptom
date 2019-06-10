package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_PREVIEW_WIDTH;

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

    public static WheelsVelocities scaleVelocityAccordingToMarkersPicturePosition(MarkerDetection detection, WheelsVelocities velocities) {
        double scale = WheelsVelocities.getSpeedScaleFromMarkerPicturePosition(detection);
        System.out.println("------------------------------> scale: " + scale);
        velocities = WheelsVelocities.scaleVelocity(velocities, scale);
        return velocities;
    }

    private static WheelsVelocities scaleVelocity(WheelsVelocities velocities, double scale) {
        return new WheelsVelocities(velocities.getFrontLeft() * scale,
                velocities.getFrontRight() * scale,
                velocities.getRearLeft() * scale,
                velocities.getRearRight() * scale);
    }

    private static double getSpeedScaleFromMarkerPicturePosition(MarkerDetection detection) {
        double[] xCorners = detection.getXCorners();
        double averagePicturePosition = (xCorners[0] + xCorners[1] + xCorners[2] + xCorners[3]) / 4.0 - CAM_PREVIEW_WIDTH / 2.0;    // 0 is the middle of the picture

        double scale = 1.0 - Math.abs(averagePicturePosition) / (CAM_PREVIEW_WIDTH / 2.0);

//        System.out.printf("avg picture position: %.2f\n", averagePicturePosition);
        if (Math.abs(averagePicturePosition) > 20) scale = 0.0;

        assert scale >= 0.0 && scale <= 1.0;
        return scale;
    }

    @Override
    public String toString() {
        return String.format("v = (%.2f, %.2f,%.2f, %.2f)", frontLeft, frontRight, rearLeft, rearRight);
    }
}
