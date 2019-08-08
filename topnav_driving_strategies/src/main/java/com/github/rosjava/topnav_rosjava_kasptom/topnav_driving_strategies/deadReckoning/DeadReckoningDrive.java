package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver.RotateManeuver;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver.StraightLineManeuver;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import std_msgs.UInt64;
import topnav_msgs.AngleRangesMsg;

import java.util.Arrays;
import java.util.HashMap;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.MAX_VELOCITY_DELTA;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.TOO_CLOSE_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class DeadReckoningDrive implements IDeadReckoningDrive {

    private double axisLength;
    private double wheelDiameter;
    private long fullRobotRotationTimeMs;
    private boolean isRunning;
    private long maneuverStartTimestamp;

    private IManeuver currentManeuver;
    private WheelsVelocitiesChangeListener wheelsListener;
    private IDeadReckoningManeuverListener maneuverListener;

    private final HashMap<String, IManeuver> maneuvers;

    public DeadReckoningDrive(double axisLength, double wheelDiameter, long fullRobotRotationTimeMs) {
        this.setWheelsParameters(axisLength, wheelDiameter, fullRobotRotationTimeMs);
        this.maneuvers = initializeManeuvers();
    }

    @Override
    public void setWheelsParameters(double axisLength, double wheelDiameter, long fullRobotRotationMilliseconds) {
        this.axisLength = axisLength;
        this.wheelDiameter = wheelDiameter;
        this.fullRobotRotationTimeMs = fullRobotRotationMilliseconds;
    }

    @Override
    public void setManeuverFinishListener(IDeadReckoningManeuverListener listener) {
        maneuverListener = listener;
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsListener = listener;
    }

    @Override
    public void startManeuver(String maneuverName, double angleDegrees, double distanceMeters) {
        System.out.printf("starting maneuver %s, %.2f°, %.2f m\n", maneuverName, angleDegrees, distanceMeters);
        currentManeuver = maneuvers.get(maneuverName);
        currentManeuver.start(angleDegrees, distanceMeters);
        maneuverStartTimestamp = System.currentTimeMillis();
        isRunning = true;
    }

    @Override
    public void onAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        if(!isRunning) {
            return;
        }

        boolean isObstacleTooClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);

        if (isObstacleTooClose) {
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            finnishManeuver(false);
        }
    }

    @Override
    public void onTickerMessage(UInt64 tickerMessage) {
        if (!isRunning) return;

        long millisecondsSinceStart = getMillisecondSinceStart();
        if (currentManeuver.isFinished(millisecondsSinceStart)) {
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            finnishManeuver(true);
        } else {
            WheelsVelocities velocities = currentManeuver.getNextVelocity(millisecondsSinceStart);
            wheelsListener.onWheelsVelocitiesChanged(velocities);
        }
    }

    private HashMap<String, IManeuver> initializeManeuvers() {
        HashMap<String, IManeuver> maneuvers = new HashMap<>();
        maneuvers.put(VALUE_MANEUVER_NAME_FORWARD, new StraightLineManeuver(axisLength, wheelDiameter, fullRobotRotationTimeMs, MAX_VELOCITY_DELTA, MAX_VELOCITY_DELTA));
        maneuvers.put(VALUE_MANEUVER_NAME_BACKWARD, new StraightLineManeuver(axisLength, wheelDiameter, fullRobotRotationTimeMs, -MAX_VELOCITY_DELTA, -MAX_VELOCITY_DELTA));
        maneuvers.put(VALUE_MANEUVER_NAME_ROTATE, new RotateManeuver(fullRobotRotationTimeMs, MAX_VELOCITY_DELTA));
        return maneuvers;
    }

    private void finnishManeuver(boolean isWithoutObstacles) {
        isRunning = false;
        maneuverListener.onManeuverFinished(isWithoutObstacles);
    }

    private long getMillisecondSinceStart() {
        return System.currentTimeMillis() - maneuverStartTimestamp;
    }
}
