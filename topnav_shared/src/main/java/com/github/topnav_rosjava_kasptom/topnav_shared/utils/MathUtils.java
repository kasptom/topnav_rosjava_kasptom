package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import java.util.Arrays;

public class MathUtils {

    /**
     * Use with caution - works for calculating modulo for DriveAlongWallStrategy
     *
     * @param divident
     * @param divisor
     * @return
     */
    public static double modulo(double divident, double divisor) {
        return (divisor - Math.signum(divident) * (divident % divisor)) % 360;
    }

    public static void main(String[] args) {
        double[] angles = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110,
                115, 120, 125, 130, 135, 140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200, 205, 210, 215,
                220, 225, 230, 235, 240, 245, 250, 255, 260, 265, 270, 275, 280, 285, 290, 295, 300, 305, 310, 315, 320,
                325, 330, 335, 340, 345, 350, 355, 360};
        double targetAngle = 270;

        System.out.printf("Wall angle, (-angle - %.2f[°]) mod 360", targetAngle);
        Arrays.stream(angles)
                .forEach(ang -> System.out.printf("%6.2f[°]: %6.2f\n", ang, modulo(-ang - targetAngle, 360)));
    }
}
