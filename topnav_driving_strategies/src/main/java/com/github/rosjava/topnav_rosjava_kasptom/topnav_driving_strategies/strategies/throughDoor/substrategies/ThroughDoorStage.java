package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies;

public enum ThroughDoorStage {
    DETECT_MARKER,
    ALIGN_BETWEEN_DOOR, // v1
    ROTATE_FRONT_AGAINST_DOOR, // v1
    TRACK_MARKER, // v2
    DRIVE_THROUGH_DOOR,
}