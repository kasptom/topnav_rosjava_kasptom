package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;

import java.util.Arrays;
import java.util.HashMap;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.TRACK_MARKER;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.NOT_DETECTED_LIMIT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.TOO_CLOSE_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.*;

public class TrackMarkerSubStrategy extends BaseSubStrategy implements IArUcoHeadTracker.TrackedMarkerListener {
    private final Log log;
    private PdVelocityCalculator velocityCalculator;
    private int notDetectedCounter = 0;

    public TrackMarkerSubStrategy(WheelsVelocitiesChangeListener wheelsListener,
                           HeadRotationChangeRequestListener headListener,
                           SubStrategyListener subStrategyListener,
                           StrategyFinishedListener finishListener,
                           HashMap<String, GuidelineParam> guidelineParamsMap, Log log) {
        super(wheelsListener, headListener, subStrategyListener, finishListener, guidelineParamsMap);
        this.log = log;
        velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    }

    @Override
    public void startStrategy() {
        super.startStrategy();
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        isObstacleTooClose = Arrays.stream(angleRangesMsg.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE / 2.0);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        if (isObstacleTooClose) {
            wheelsListener.onWheelsVelocitiesChanged(MOVE_BACK_VELOCITY);
            return;
        }

        double range = ArucoMarkerUtils.distanceTo(detection);
        WheelsVelocities velocities;
        if (isDoorMarker(detection, KEY_FRONT_LEFT_MARKER_ID)) {
            velocities = velocityCalculator.calculateRotationSpeed(headRotation, range, System.nanoTime(), 90, 0.3);
        } else if (isDoorMarker(detection, KEY_FRONT_RIGHT_MARKER_ID)) {
            velocities = velocityCalculator.calculateRotationSpeed(headRotation, range, System.nanoTime(), -90, 0.3);
        } else {
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            notDetectedCounter++;
            if (notDetectedCounter >= NOT_DETECTED_LIMIT) {
                log.info("not detected counter reached its limit");
                subStrategyListener.onStageFinished(TRACK_MARKER, RelativeDirection.BEHIND);
            }
            return;
        }

        notDetectedCounter = 0;
        velocities = WheelsVelocities.addVelocities(BASE_ROBOT_VELOCITY, velocities);

        double scale = WheelsVelocities.getSpeedScaleFromMarkerPicturePosition(detection);
        System.out.println("------------------------------> scale: " + scale);
        velocities = WheelsVelocities.scaleVelocity(velocities, scale);

        wheelsListener.onWheelsVelocitiesChanged(velocities);
    }

    private boolean isDoorMarker(MarkerDetection detection, String doorMarkerParamKey) {
        return detection.getId().equalsIgnoreCase(guidelineParamsMap.get(doorMarkerParamKey).getValue());
    }
}
