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
    public static final String DRIVING_STRATEGY_ACCORDING_TO_MARKER = "DS_ACCORDING_TO_MARKER";

    public static final HashSet<String> DRIVING_STRATEGIES = new HashSet<>(Arrays.asList(
            DRIVING_STRATEGY_IDLE,
            DRIVING_STRATEGY_ALONG_WALL_2,
            DRIVING_STRATEGY_STOP_BEFORE_WALL,
            DRIVING_STRATEGY_PASS_THROUGH_DOOR_2,
            DRIVING_STRATEGY_APPROACH_MARKER,
            DRIVING_STRATEGY_TRACK_ARUCOS,
            DRIVING_STRATEGY_DEAD_RECKONING_TEST,
            DRIVING_STRATEGY_ACCORDING_TO_MARKER));

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

    public class DeadReckoning {
        public static final String KEY_MANEUVER_NAME = "DS_PARAM_MANEUVER_NAME";
        public static final String VALUE_MANEUVER_NAME_FORWARD = "forward";
        public static final String VALUE_MANEUVER_NAME_BACKWARD = "backward";
        public static final String VALUE_MANEUVER_NAME_ROTATE = "rotate";
        public static final String VALUE_MANEUVER_NAME_AROUND_CIRCLE = "around";

        public static final String KEY_MANEUVER_ANGLE_DEGREES = "DS_PARAM_MANEUVER_AMGLE_DEGREES";
        public static final String KEY_MANEUVER_DISTANCE_METERS = "DS_PARAM_MANEUVER_DIST_METERS";
        public static final String KEY_MANEUVER_WHEEL_FULL_ROTATION_MS = "DS_PARAM_MANEUVER_WHEEL_FULL_ROTATION_MS";
    }

    public class ApproachMarker {
        public static final String KEY_APPROACHED_MARKER_ID = "DS_PARAM_APPROACHED_MARKER_ID";
    }

    public class PositionAccordingToMarker {
        public static final String KEY_ACCORDING_MARKER_ID = "DS_PARAM_ACCORDING_MARKER_ID";

        public static final String KEY_ACCORDING_ALIGNMENT = "DS_PARAM_ACCORDING_ALIGNMENT";
        public static final String VALUE_ACCORDING_ALIGNMENT_LEFT = "left";
        public static final String VALUE_ACCORDING_ALIGNMENT_RIGHT = "right";
        public static final String VALUE_ACCORDING_ALIGNMENT_CENTER = "center";

        public static final String KEY_ACCORDING_DIRECTION = "DS_PARAM_ACCORDING_DIRECTION";
        public static final String VALUE_ACCORDING_DIRECTION_AHEAD = "ahead";
        public static final String VALUE_ACCORDING_DIRECTION_BEHIND = "behind";
        public static final String VALUE_ACCORDING_DIRECTION_TO_LEFT = "to_left";
        public static final String VALUE_ACCORDING_DIRECTION_TO_RIGHT = "to_right";

    }

    public static final List<String> PARAM_NAMES = Arrays.asList(ThroughDoor.KEY_FRONT_LEFT_MARKER_ID,
            ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID,
            ThroughDoor.KEY_BACK_LEFT_MARKER_ID,
            ThroughDoor.KEY_BACK_RIGHT_MARKER_ID,
            FollowWall.KEY_TRACKED_WALL_ALIGNMENT,
            ApproachMarker.KEY_APPROACHED_MARKER_ID,
            DeadReckoning.KEY_MANEUVER_NAME,
            DeadReckoning.KEY_MANEUVER_ANGLE_DEGREES,
            DeadReckoning.KEY_MANEUVER_DISTANCE_METERS,
            DeadReckoning.KEY_MANEUVER_WHEEL_FULL_ROTATION_MS,
            PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID,
            PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT,
            PositionAccordingToMarker.KEY_ACCORDING_DIRECTION);
}
