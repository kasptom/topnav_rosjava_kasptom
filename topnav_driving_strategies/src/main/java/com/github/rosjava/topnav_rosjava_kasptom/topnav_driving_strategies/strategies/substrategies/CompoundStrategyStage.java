package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

public enum CompoundStrategyStage {
    INITIAL,

    DETECT_MARKER,
    ALIGN_BETWEEN_DOOR, // v1, v3
    ROTATE_FRONT_AGAINST_DOOR, // v1
    TRACK_MARKER, // v2
    DRIVE_THROUGH_DOOR, // v1, v2. v3
    APPROACH_MARKER,

    LOOK_AROUND_FOR_MARKER,
    LOOKING_AT_MARKER,
    MANEUVER,
}
