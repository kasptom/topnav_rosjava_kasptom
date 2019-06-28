package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.approachMarker;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.BaseSubStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.SubStrategyListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
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
    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createPdVelocityCalculator(0.5, 1.0, 1.0, 1.0);
    private int notDetectedCounter = 0;
    private static final double TARGET_APPROACH_RANGE_METERS = 0.5;

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
        WheelsVelocities velocities;
        if (isTrackedMarker(detection)) {
            double range = ArucoMarkerUtils.distanceTo(detection);

            System.out.printf("head rotation %.2f\n", headRotation);
            velocities = velocityCalculator.calculateRotationSpeed(headRotation, range, System.nanoTime(), 0, TARGET_APPROACH_RANGE_METERS);

            log.info(String.format("Range to the target marker: %.2f", range));

            if (range <= TARGET_APPROACH_RANGE_METERS) {
                log.info(String.format("Target approach range reached: %.2f (%.2f)", range, TARGET_APPROACH_RANGE_METERS));
                wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
                subStrategyListener.onStageFinished(APPROACH_MARKER, RelativeDirection.AHEAD);
                return;
            }
        } else {
            wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
            notDetectedCounter++;
            if (notDetectedCounter >= NOT_DETECTED_LIMIT) {
                log.info("not detected counter reached its limit");
                finishListener.onStrategyFinished(false);
            }
            return;
        }

        notDetectedCounter = 0;
        velocities = WheelsVelocities.addVelocities(BASE_ROBOT_VELOCITY, velocities);

        WheelsVelocities.scaleVelocityAccordingToMarkersPicturePosition(detection, velocities);
//        System.out.println(velocities);

        wheelsListener.onWheelsVelocitiesChanged(velocities);
    }

    private boolean isTrackedMarker(MarkerDetection detection) {
        return detection.getId().equalsIgnoreCase(guidelineParamsMap.get(KEY_APPROACHED_MARKER_ID).getValue());
    }
}
