package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

public class TopicNames {
    public static final String HEAD_JOINT_TOPIC = "/capo_head_rotation_controller/command";
    public static final String HEAD_RELATIVE_DIRECTION_CHANGE_TOPIC = "/topnav/head_relative_direction_state";
    public static final String TOPNAV_FEEDBACK_TOPIC = "/topnav/feedback";

    public static final String TOPNAV_NAVIGATION_HEAD_DIRECTION_TOPIC = "/topnav/naviation/relative_direction";
    public static final String TOPNAV_STRATEGY_HEAD_DIRECTION_TOPIC = "/topnav/driving/relative_direction";

    public static final String CAPO_JOINT_STATES = "/joint_states";

    public static final String TOPNAV_HOUGH_TOPIC = "/capo/laser/hough";
    public static final String TOPNAV_ANGLE_RANGE_TOPIC = "/capo/laser/angle_range";

    public static final String GAZEBO_LASER_SCAN_TOPIC = "/capo/laser/scan";
}
