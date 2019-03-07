package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

public class Limits {
    public static final int LIDAR_SAMPLES_COUNT = 61;
    public static double TOO_CLOSE_RANGE = 0.3;
    public static double DOOR_DETECTION_RANGE = 1.0; // meters

    public static final double BASE_VELOCITY = 2.0;
    public static final double MAX_VELOCITY_DELTA = 2.0;

    public static final double PARALLEL_TO_LEFT_WALL_ANGLE = 270; // Hough acc's domain
    public static final double AHEAD_THE_WALL = 180; // Hough acc's domain

    public static final double LIDAR_MIN_RANGE = 0.2;
    public static final double LIDAR_MAX_RANGE = 5.0;

    public static final double CASE_WIDTH = 0.2032;
    public static final double WHEEL_WIDTH = 0.05;
}
