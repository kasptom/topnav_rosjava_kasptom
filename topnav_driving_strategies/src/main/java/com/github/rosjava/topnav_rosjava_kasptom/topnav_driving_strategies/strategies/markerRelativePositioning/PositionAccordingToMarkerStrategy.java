package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.markerRelativePositioning;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.DeadReckoningDrive;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IDeadReckoningDrive;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IDeadReckoningManeuverListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class PositionAccordingToMarkerStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener, IDeadReckoningManeuverListener {

    private final IArUcoHeadTracker arUcoTracker;
    private StrategyFinishedListener finishedListener;
    private final WheelsVelocitiesChangeListener wheelsVelocitiesChangeListener;

    double fullRotationTimeMilliseconds;

    private CompoundStrategyStage currentStage;

    private final HashMap<String, GuidelineParam> guidelineParamsMap;
    private final Log log;
    private IDeadReckoningDrive deadReckoningDrive = null;

    private boolean isObstacleTooClose;

    public PositionAccordingToMarkerStrategy(IArUcoHeadTracker arUcoTracker, WheelsVelocitiesChangeListener wheelsVelocitiesChangeListener, Log log) {
        guidelineParamsMap = new HashMap<>();
        this.arUcoTracker = arUcoTracker;
        this.wheelsVelocitiesChangeListener = wheelsVelocitiesChangeListener;
        this.log = log;
        currentStage = CompoundStrategyStage.INITIAL;
    }

    @Override
    public void startStrategy() {
        LinkedHashSet<String> markerIds = GuidelineUtils.asOrderedDoorMarkerIds(guidelineParamsMap);
        markerIds.addAll(GuidelineUtils.approachedMarkerIdAsSet(guidelineParamsMap));
        markerIds.remove(GuidelineParam.getEmptyParam().getValue());
        arUcoTracker.setTrackedMarkers(markerIds);
        deadReckoningDrive = initializeDeadReckoningDrive();

        arUcoTracker.start();
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {

    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleTooClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);

        if (isObstacleTooClose) {
            wheelsVelocitiesChangeListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            finishedListener.onStrategyFinished(false);
        }
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {

    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {

    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeRequestListener listener) {
    }

    @Override
    public void setStrategyFinishedListener(StrategyFinishedListener listener) {
        finishedListener = listener;
    }

    @Override
    public void setGuidelineParameters(List<String> parameters) {
        GuidelineUtils.reloadParameters(parameters, guidelineParamsMap);
        fullRotationTimeMilliseconds = Long.parseLong(guidelineParamsMap.get(KEY_MANEUVER_WHEEL_FULL_ROTATION_MS).getValue());
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        System.out.printf("tracked marker update: %s, at: %.2f[deg], distance: %.2f",
                detection.getId(),
                detection.getDetectedAtAngle(),
                ArucoMarkerUtils.distanceTo(detection));
        // TODO handle detection / switch stages or finish
    }

    @Override
    public void onManeuverFinished(boolean isWithoutObstacles) {
        if (!isWithoutObstacles) {
            finishedListener.onStrategyFinished(false);
        }
    }

    private IDeadReckoningDrive initializeDeadReckoningDrive() {
        long fullRotationTimeMilliseconds = Long.parseLong(guidelineParamsMap.get(KEY_MANEUVER_WHEEL_FULL_ROTATION_MS).getValue());

        IDeadReckoningDrive deadReckoningDrive = new DeadReckoningDrive(CASE_WIDTH + WHEEL_WIDTH, WHEEL_DIAMETER, fullRotationTimeMilliseconds);
        deadReckoningDrive.setWheelsVelocitiesListener(wheelsVelocitiesChangeListener);
        deadReckoningDrive.setManeuverFinishListener(this);
        deadReckoningDrive.setWheelsParameters(CASE_WIDTH + WHEEL_WIDTH, WHEEL_DIAMETER, fullRotationTimeMilliseconds);
        return deadReckoningDrive;
    }

    private void startManeuver() {
        String maneuverName = guidelineParamsMap.get(KEY_MANEUVER_NAME).getValue();
        double angleDegrees = Double.parseDouble(guidelineParamsMap.get(KEY_MANEUVER_ANGLE_DEGREES).getValue());
        double distanceMeters = Double.parseDouble(guidelineParamsMap.get(KEY_MANEUVER_DISTANCE_METERS).getValue());
        deadReckoningDrive.startManeuver(maneuverName, angleDegrees, distanceMeters);
    }
}
