package com.github.topnav_rosjava_kasptom.topnav_shared.constants;

import java.util.Arrays;
import java.util.HashSet;

public class DrivingStrategy {
    public static final String DRIVING_STRATEGY_IDLE = "DS_IDLE";
    public static final String DRIVING_STRATEGY_ALONG_WALL = "DS_ALONG_WALL";
    public static final String DRIVING_STRATEGY_STOP_BEFORE_WALL = "DS_STOP_WALL";
    public static final HashSet<String> DRIVING_STRATEGIES = new HashSet<>(Arrays.asList(DRIVING_STRATEGY_IDLE, DRIVING_STRATEGY_ALONG_WALL, DRIVING_STRATEGY_STOP_BEFORE_WALL));
}
