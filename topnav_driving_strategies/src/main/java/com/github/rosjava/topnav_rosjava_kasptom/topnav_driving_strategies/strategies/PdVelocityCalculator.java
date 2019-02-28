package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.CASE_WIDTH;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.WHEEL_WIDTH;

class PdVelocityCalculator {
    private double prevTimestamp;
    private double prevAngle;
    private double prevRange;

    private double propCoefAngle;

    private double propCoefDist;
    private double derivCoefAngle;
    private double derivCoefDist;
    private final double axisLength;

    private PdVelocityCalculator(double propCoefAngle, double propCoefDist, double derivCoefAngle, double derivCoefDist, double axisLength) {
        this.propCoefAngle = propCoefAngle;
        this.propCoefDist = propCoefDist;
        this.derivCoefAngle = derivCoefAngle;
        this.derivCoefDist = derivCoefDist;
        this.axisLength = axisLength;
        prevTimestamp = System.nanoTime();
        prevAngle = 0.0;
        prevRange = 0.0;
    }

    static PdVelocityCalculator createDefaultPdVelocityCalculator() {
        double axisLength = CASE_WIDTH + WHEEL_WIDTH;
        return new PdVelocityCalculator(0.5, 1.0, 1.0, 1.0, axisLength / 2.0);
    }

    void updateCoefficients(double propCoefAngle, double derivCoefAngle, double propCoefDist, double derivCoefDist) {
        this.propCoefAngle = propCoefAngle;
        this.derivCoefAngle = derivCoefAngle;
        this.propCoefDist = propCoefDist;
        this.derivCoefDist = derivCoefDist;
    }

    WheelsVelocities calculateRotationSpeed(double angle, double range, long timestamp, double targetAngle, @SuppressWarnings("SameParameterValue") double targetRange) {
        double angularVelocityAngle = calculateRotationSpeedForAngle(angle, timestamp, targetAngle);
        double angularVelocityRange = calculateRotationSpeedForRange(range, timestamp, targetRange, angle, targetAngle);

//        System.out.printf("Angle: %.2f, target %.2f, distance: %.2f, target: %.2f ", angle, targetAngle, range, targetRange);
//        System.out.printf("Angular velocities: angle=%.2f, range=%.2f\n", angularVelocityAngle, angularVelocityRange);

        double linearVelocity = (angularVelocityAngle + angularVelocityRange) * (axisLength / 2.0);
        double halfLinearVelocity = linearVelocity / 2.0;

        prevTimestamp = timestamp;
        prevAngle = angle;
        prevRange = range;

        return new WheelsVelocities(-halfLinearVelocity, halfLinearVelocity, -halfLinearVelocity, halfLinearVelocity);
    }

    private double calculateRotationSpeedForRange(double range, long timestamp, double targetRange, double angle, double targetAngle) {
        double rangeInducedAngleOffset = 90 * (targetRange - range) / range;
        if (rangeInducedAngleOffset > 90) {
            rangeInducedAngleOffset = 90;
        }

        if (rangeInducedAngleOffset < -90) {
            rangeInducedAngleOffset = -90;
        }

        double rangeInducedAngle = targetAngle + Math.signum(targetAngle) * rangeInducedAngleOffset;

//        System.out.printf("range induced angle %.2f\n", rangeInducedAngle);
        double errorCorrection = derivCoefDist * (1.0 / (timestamp - prevTimestamp)) * (range - prevRange);
        return propCoefDist * (angle - rangeInducedAngle) + errorCorrection;
    }

    private double calculateRotationSpeedForAngle(double angle, long timestamp, double targetAngle) {
        return propCoefAngle * (angle - targetAngle)
                + derivCoefAngle * (1.0 / (timestamp - prevTimestamp)) * (angle - prevAngle);
    }
}
