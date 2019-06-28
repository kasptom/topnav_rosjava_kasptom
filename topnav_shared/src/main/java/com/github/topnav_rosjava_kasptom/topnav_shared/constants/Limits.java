package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

public class Limits {
    public static final int LIDAR_SAMPLES_COUNT = 61;
    public static double TOO_CLOSE_RANGE = 0.25;
    public static double DOOR_DETECTION_RANGE = 1.0; // meters

    public static final double BASE_VELOCITY = 2.0;
    public static final double MAX_VELOCITY_DELTA = 2.0;
    public static final double MAX_VELOCITY = BASE_VELOCITY + MAX_VELOCITY_DELTA;
    public static final double MIN_VELOCITY = -MAX_VELOCITY;

    public static final double PARALLEL_TO_LEFT_WALL_ANGLE = 270; // Hough acc's domain
    public static final double AHEAD_THE_WALL = 180; // Hough acc's domain

    public static final double LIDAR_MIN_RANGE = 0.2;
    public static final double LIDAR_MAX_RANGE = 5.0;

//    public static final double CASE_WIDTH = 0.2032;   // capo (1)
//    public static final double WHEEL_WIDTH = 0.05;    // capo (1)
    public static final double CASE_WIDTH = 0.135;    // capo2
    public static final double WHEEL_WIDTH = 0.01;  // capo2

    public static final int NOT_DETECTED_LIMIT = 50;   // allowed number of messages with the "-1" ArUco detections in a row

    public static final double AHEAD_OBSTACLE_ANGLE = 0.0;
    public static final double TARGET_WALL_RANGE = 0.5;

    public static final double MIN_SEARCH_ANGLE = -180.0;
    public static final double MAX_SEARCH_ANGLE = 180.0;

    public static final double SEARCH_ANGLE_STEP_DEGREES = 30.0;
    public static final long CAMERA_LATENCY_MS = 1000;
}
