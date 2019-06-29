package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DrivingStrategy {
    public static final String DRIVING_STRATEGY_IDLE = "DS_IDLE";
    public static final String DRIVING_STRATEGY_ALONG_WALL_2 = "DS_ALONG_WALL_2";
    public static final String DRIVING_STRATEGY_STOP_BEFORE_WALL = "DS_STOP_WALL";
    public static final String DRIVING_STRATEGY_TRACK_ARUCOS = "DS_TEST_TRACK_ARUCOS";
    public static final String DRIVING_STRATEGY_PASS_THROUGH_DOOR_2 = "DS_PASS_THROUGH_DOOR_2";
    public static final String DRIVING_STRATEGY_APPROACH_MARKER = "DS_APPROACH_MARKER";
    public static final String DRIVING_STRATEGY_DEAD_RECKONING_TEST = "DS_DEAD_RECKONING_TEST";

    public static final HashSet<String> DRIVING_STRATEGIES = new HashSet<>(Arrays.asList(
            DRIVING_STRATEGY_IDLE,
            DRIVING_STRATEGY_ALONG_WALL_2,
            DRIVING_STRATEGY_STOP_BEFORE_WALL,
            DRIVING_STRATEGY_PASS_THROUGH_DOOR_2,
            DRIVING_STRATEGY_APPROACH_MARKER,
            DRIVING_STRATEGY_TRACK_ARUCOS));

    public static final String REACTIVE_DRIVING_STRATEGY_MOVE_BACK = "RDS_MOVE_BACK";

    public static final HashSet<String> MARKER_PARAMS = new HashSet<>(Arrays.asList(
            ThroughDoor.KEY_BACK_LEFT_MARKER_ID,
            ThroughDoor.KEY_BACK_RIGHT_MARKER_ID,
            ThroughDoor.KEY_FRONT_LEFT_MARKER_ID,
            ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID,
            ApproachMarker.KEY_APPROACHED_MARKER_ID
    ));

    public class ThroughDoor {
        public static final String KEY_FRONT_LEFT_MARKER_ID = "DS_PARAM_FRONT_LEFT_MARKER_ID";
        public static final String KEY_FRONT_RIGHT_MARKER_ID = "DS_PARAM_FRONT_RIGHT_MARKER_ID";
        public static final String KEY_BACK_LEFT_MARKER_ID = "DS_PARAM_BACK_LEFT_MARKER_ID";
        public static final String KEY_BACK_RIGHT_MARKER_ID = "DS_PARAM_BACK_RIGHT_MARKER_ID";
    }

    public class FollowWall {
        public static final String KEY_TRACKED_WALL_ALIGNMENT = "DS_PARAM_TRACKED_WALL_ALIGNMENT";
        public static final String VALUE_TRACKED_WALL_LEFT = "LEFT";
        public static final String VALUE_TRACKED_WALL_RIGHT = "RIGHT";
    }

    public class DeadReckining {
        public static final String KEY_MANEUVER_NAME = "DS_PARAM_MANEUVER_NAME";
        public static final String VALUE_MANEUVER_NAME_FORWARD = "maneuver_forward";
        public static final String VALUE_MANEUVER_NAME_BACKWARD = "maneuver_backward";
        public static final String VALUE_MANEUVER_NAME_ROTATE = "maneuver_rotate";
        public static final String VALUE_MANEUVER_NAME_AROUND_CIRCLE = "maneuver_around";
    }

    public class ApproachMarker {
        public static final String KEY_APPROACHED_MARKER_ID = "DS_PARAM_APPROACHED_MARKER_ID";
    }

    public static final List<String> PARAM_NAMES = Arrays.asList(ThroughDoor.KEY_FRONT_LEFT_MARKER_ID,
            ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID,
            ThroughDoor.KEY_BACK_LEFT_MARKER_ID,
            ThroughDoor.KEY_BACK_RIGHT_MARKER_ID,
            FollowWall.KEY_TRACKED_WALL_ALIGNMENT,
            ApproachMarker.KEY_APPROACHED_MARKER_ID);
}
