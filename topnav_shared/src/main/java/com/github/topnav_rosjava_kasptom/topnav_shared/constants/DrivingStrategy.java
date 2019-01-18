package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

import java.util.Arrays;
import java.util.HashSet;

public class DrivingStrategy {
    public static final String DRIVING_STRATEGY_IDLE = "DS_IDLE";
    public static final String DRIVING_STRATEGY_ALONG_WALL = "DS_ALONG_WALL";
    public static final String DRIVING_STRATEGY_STOP_BEFORE_WALL = "DS_STOP_WALL";
    public static final String DRIVING_STRATEGY_PASS_THROUGH_DOOR = "DS_PASS_THROUGH_DOOR";
    public static final HashSet<String> DRIVING_STRATEGIES = new HashSet<>(Arrays.asList(
            DRIVING_STRATEGY_IDLE,
            DRIVING_STRATEGY_ALONG_WALL,
            DRIVING_STRATEGY_STOP_BEFORE_WALL,
            DRIVING_STRATEGY_PASS_THROUGH_DOOR));

    class ThroughDoor {
        public static final String KEY_FRONT_LEFT_MARKER_ID = "DS_KEY_PASS_THROUGH_DOOR_FRONT_LEFT_MARKER_ID";
        public static final String KEY_FRONT_RIGHT_MARKER_ID = "DS_KEY_PASS_THROUGH_DOOR_FRONT_RIGHT_MARKER_ID";
        public static final String KEY_BACK_LEFT_MARKER_ID = "DS_KEY_PASS_THROUGH_DOOR_BACK_LEFT_MARKER_ID";
        public static final String KEY_BACK_RIGHT_MARKER_ID = "DS_KEY_PASS_THROUGH_DOOR_BACK_RIGHT_MARKER_ID";
    }
}
