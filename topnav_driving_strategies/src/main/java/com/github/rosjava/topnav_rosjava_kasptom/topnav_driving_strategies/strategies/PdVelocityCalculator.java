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
        return new PdVelocityCalculator(5.0, 10.0, 2.0, 4.0, axisLength / 2.0);
    }

    public WheelsVelocities calculateRotationSpeed(double angle, double range, long timestamp, double targetAngle, double targetRange) {
        System.out.printf("Angle: %.2f, target %.2f\n", angle, targetAngle);
        double angleVelocity = propCoefAngle * (angle - targetAngle)
//                + propCoefDist * (targetRange - range)
                + (1.0 / (timestamp - prevTimestamp))
                * (derivCoefAngle * (angle - prevAngle)
//                    + derivCoefDist * (range - prevRange)
                    );

        double linearVelocity = angleVelocity * (axisLength / 2.0);
        double halfLinearVelocity = linearVelocity / 2.0;

        prevTimestamp = timestamp;
        prevAngle = angle;
        prevRange = range;

        return new WheelsVelocities(-halfLinearVelocity, halfLinearVelocity, -halfLinearVelocity, halfLinearVelocity);
    }
}
