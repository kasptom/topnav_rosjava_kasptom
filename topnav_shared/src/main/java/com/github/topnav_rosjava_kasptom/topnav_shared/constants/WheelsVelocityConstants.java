package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

public class WheelsVelocityConstants {
    public static final WheelsVelocities ZERO_VELOCITY = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
    public static final WheelsVelocities MOVE_BACK_VELOCITY = new WheelsVelocities(-0.5, -0.5, -0.5, -0.5);
    public static final WheelsVelocities BASE_ROBOT_VELOCITY = new WheelsVelocities(4.0, 4.0, 4.0, 4.0);
    public static final WheelsVelocities ROTATE_CLOCKWISE_VELOCITY = new WheelsVelocities(1.5, -1.5, 1.5, -1.5);
}
