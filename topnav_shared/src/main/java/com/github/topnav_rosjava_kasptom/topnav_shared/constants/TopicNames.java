package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

public class TopicNames {
    public static final String TOPNAV_MAIN_CONTROLLER_NODE_NAME = "topnav/main_controller";

    public static final String HEAD_RELATIVE_DIRECTION_CHANGE_TOPIC = "/topnav/head/relative_direction_state";
    public static final String HEAD_LINEAR_DIRECTION_CHANGE_TOPIC = "/topnav/head/linear_direction_state";
    public static final String HEAD_TIME_MS_SINCE_LAST_ROTATION_TOPIC = "/topnav/head/time_ms_since_last_rotation";

    public static final String TOPNAV_NAVIGATION_HEAD_DIRECTION_TOPIC = "/topnav/naviation/relative_direction";
    public static final String TOPNAV_STRATEGY_HEAD_DIRECTION_TOPIC = "/topnav/driving/relative_direction";

    public static final String TOPNAV_FEEDBACK_TOPIC = "/topnav/feedback";
    public static final String TOPNAV_STRATEGY_CHANGE_TOPIC = "/topnav/strategy_change";

    public static final String TOPNAV_GUIDELINES_TOPIC = "/topnav/guidelines";
    public static final String TOPNAV_CONFIG_TOPIC = "/topnav/config";

    public static final String HEAD_JOINT_TOPIC = "/capo_head_rotation_controller/command";
    public static final String CAPO_JOINT_STATES = "/joint_states";

    public static final String TOPNAV_CAPO_TICKER_TOPIC = "/capo/ticker";
    public static final String GAZEBO_LASER_SCAN_TOPIC = "/capo/laser/scan";

    public static final String TOPNAV_NAVIGATION_MANUAL_STEERING_TOPIC = "topnav/manual_steering";

    public static final String TOPNAV_HOUGH_TOPIC = "/converter/laser/hough";
    public static final String TOPNAV_ANGLE_RANGE_TOPIC = "/converter/laser/angle_range";
    public static final String TOPNAV_ARUCO_TOPIC = "/converter/camera1/aruco";
}
