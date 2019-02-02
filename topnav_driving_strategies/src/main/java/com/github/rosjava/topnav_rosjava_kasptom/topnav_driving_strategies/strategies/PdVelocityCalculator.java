package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

public class PdVelocityCalculator {
    public static final double CASE_WIDTH = 0.2032;
    public static final double WHEEL_WIDTH = 0.05;

    private double prevTimestamp;
    private double prevAngle;
    private double prevRange;

    private final double propCoefAngle;
    private final double propCoefDist;
    private final double derivCoefAngle;
    private final double derivCoefDist;
    private final double axisLength;

    public PdVelocityCalculator(double propCoefAngle, double propCoefDist, double derivCoefAngle, double derivCoefDist, double axisLength) {
        this.propCoefAngle = propCoefAngle;
        this.propCoefDist = propCoefDist;
        this.derivCoefAngle = derivCoefAngle;
        this.derivCoefDist = derivCoefDist;
        this.axisLength = axisLength;
        prevTimestamp = System.nanoTime();
        prevAngle = 0.0;
        prevRange = 0.0;
    }

    public static PdVelocityCalculator createDefaultPdVelocityCalculator() {
        double axisLength = CASE_WIDTH + WHEEL_WIDTH;
        return new PdVelocityCalculator(5.0, 20.0, 2.0, 4.0, axisLength / 2.0);
    }

    public WheelsVelocities calculateRotationSpeed(double angle, double range, long timestamp, double targetAngle, double targetRange) {
        double angularVelocityAngle = calculateRotationSpeedForAngle(angle, timestamp, targetAngle);
        double angularVelocityRange = calculateRotationSpeedForRange(range, timestamp, targetRange);

        System.out.printf("Angle: %.2f, target %.2f, distance: %.2f, target: %.2f\n", angle, targetAngle, range, targetRange);
        System.out.printf("Angular velocities: angle=%.2f, range=%.2f\n", angularVelocityAngle, angularVelocityRange);

        double linearVelocity = (angularVelocityAngle + angularVelocityRange) * (axisLength / 2.0);
        double halfLinearVelocity = linearVelocity / 2.0;

        prevTimestamp = timestamp;
        prevAngle = angle;
        prevRange = range;

        return new WheelsVelocities(-halfLinearVelocity, halfLinearVelocity, -halfLinearVelocity, halfLinearVelocity);
    }

    private double calculateRotationSpeedForRange(double range, long timestamp, double targetRange) {
        return propCoefDist * (targetRange - range)
                + (1.0 / (timestamp - prevTimestamp)) * derivCoefDist * (range - prevRange);
    }

    private double calculateRotationSpeedForAngle(double angle, long timestamp, double targetAngle) {
        return propCoefAngle * (angle - targetAngle)
                + (1.0 / (timestamp - prevTimestamp)) * derivCoefAngle * (angle - prevAngle);
    }
}
