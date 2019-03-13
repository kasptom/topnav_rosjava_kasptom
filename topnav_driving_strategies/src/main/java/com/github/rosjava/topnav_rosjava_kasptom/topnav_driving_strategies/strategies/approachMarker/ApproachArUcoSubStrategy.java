package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.approachMarker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.BaseSubStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.SubStrategyListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;

import java.util.HashMap;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.APPROACH_MARKER;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.NOT_DETECTED_LIMIT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.BASE_ROBOT_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class ApproachArUcoSubStrategy extends BaseSubStrategy implements IArUcoHeadTracker.TrackedMarkerListener {

    private final Log log;
    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();
    private int notDetectedCounter = 0;

    ApproachArUcoSubStrategy(WheelsVelocitiesChangeListener wheelsListener,
                             HeadRotationChangeRequestListener headListener,
                             SubStrategyListener subStrategyListener,
                             StrategyFinishedListener finishListener,
                             HashMap<String, GuidelineParam> guidelineParamsMap, Log log) {
        super(wheelsListener, headListener, subStrategyListener, finishListener, guidelineParamsMap);
        this.log = log;
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {

    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {

    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        double range = Math.sqrt(Math.pow(detection.getCameraPosition()[0], 2) + Math.pow(detection.getCameraPosition()[2], 2));
        WheelsVelocities velocities;
        if (isTrackedMarker(detection)) {
            velocities = velocityCalculator.calculateRotationSpeed(headRotation, range, System.nanoTime(), 0, 0.4);
        } else {
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            notDetectedCounter++;
            if (notDetectedCounter >= NOT_DETECTED_LIMIT) {
                log.info("not detected counter reached its limit");
                subStrategyListener.onStageFinished(APPROACH_MARKER, RelativeDirection.AHEAD);
            }
            return;
        }

        notDetectedCounter = 0;
        velocities = WheelsVelocities.addVelocities(BASE_ROBOT_VELOCITY, velocities);
        wheelsListener.onWheelsVelocitiesChanged(velocities);
    }

    private boolean isTrackedMarker(MarkerDetection detection) {
        return detection.getId().equalsIgnoreCase(guidelineParamsMap.get(KEY_APPROACHED_MARKER_ID).getValue());
    }
}
