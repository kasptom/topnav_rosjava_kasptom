package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.BASE_VELOCITY;

public class WheelsVelocityConstants {
    public static final WheelsVelocities ZERO_VELOCITY = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
    public static final WheelsVelocities MOVE_BACK_VELOCITY = new WheelsVelocities(-2.0, -2.0, -2.0, -2.0);
    public static final WheelsVelocities BASE_ROBOT_VELOCITY = new WheelsVelocities(BASE_VELOCITY, BASE_VELOCITY, BASE_VELOCITY, BASE_VELOCITY);
    public static final WheelsVelocities ROTATE_CLOCKWISE_VELOCITY = new WheelsVelocities(1.5, -1.5, 1.5, -1.5);
}
