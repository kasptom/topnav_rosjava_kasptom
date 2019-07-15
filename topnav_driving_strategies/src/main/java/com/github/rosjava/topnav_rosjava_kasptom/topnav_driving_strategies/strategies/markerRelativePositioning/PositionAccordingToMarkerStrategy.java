package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.markerRelativePositioning;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.*;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker.headTracker.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.DeadReckoningDrive;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IDeadReckoningDrive;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.IDeadReckoningManeuverListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.ManeuverDescription;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver.IManeuverDescriptionGenerator;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver.ManeuverDescriptionGenerator;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver.ManeuverUtils;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.*;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.ArucoMarkerUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import std_msgs.UInt64;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.*;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.CAM_PREVIEW_WIDTH;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class PositionAccordingToMarkerStrategy implements IDrivingStrategy, IArUcoHeadTracker.TrackedMarkerListener, IDeadReckoningManeuverListener, IClockMessageHandler {

    private final IArUcoHeadTracker arUcoTracker;
    private final IManeuverDescriptionGenerator maneuverGenerator;
    private StrategyFinishedListener finishedListener;
    private WheelsVelocitiesChangeListener wheelsVelocitiesChangeListener;

    private CompoundStrategyStage currentStage;

    private final HashMap<String, GuidelineParam> guidelineParamsMap;
    private final Log log;
    private IDeadReckoningDrive deadReckoningDrive = null;

    private long earliestCenteredOnTimeStamp = Long.MAX_VALUE;

    private boolean isObstacleTooClose;

    private String markerId;
    private RelativeDirection requestedRelativeDirection;
    private RelativeAlignment requestedRelativeAlignment;
    private Queue<ManeuverDescription> maneuverDescriptions;

    public PositionAccordingToMarkerStrategy(IArUcoHeadTracker arUcoTracker, Log log) {
        guidelineParamsMap = new HashMap<>();
        this.arUcoTracker = arUcoTracker;
        this.log = log;
        currentStage = CompoundStrategyStage.INITIAL;
        maneuverGenerator = new ManeuverDescriptionGenerator();
        maneuverDescriptions = new ArrayDeque<>();
    }

    @Override
    public void startStrategy() {
        LinkedHashSet<String> markerIds = GuidelineUtils.asOrderedDoorMarkerIds(guidelineParamsMap);
        markerIds.addAll(GuidelineUtils.approachedMarkerIdAsSet(guidelineParamsMap));
        markerIds.addAll(GuidelineUtils.accordingToMarkerIdAsSet(guidelineParamsMap));
        markerIds.remove(GuidelineParam.getEmptyParam().getValue());
        arUcoTracker.setTrackedMarkers(markerIds);
        deadReckoningDrive = initializeDeadReckoningDrive();

        currentStage = CompoundStrategyStage.LOOK_AROUND_FOR_MARKER;
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
    public void handleClockMessage(UInt64 clockMsg) {
        if (isObstacleTooClose || currentStage != CompoundStrategyStage.MANEUVER) return;

        deadReckoningDrive.onClockMessage(clockMsg);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {

    }

    @Override
    public void handleHeadDirectionChange(std_msgs.String relativeDirectionMsg) {

    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsVelocitiesChangeListener = listener;
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
        requestedRelativeDirection = RelativeDirection.valueOf(
                guidelineParamsMap.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION)
                        .getValue()
                        .toUpperCase());

        requestedRelativeAlignment = RelativeAlignment.valueOf(
                guidelineParamsMap.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT)
                        .getValue()
                        .toUpperCase());

        markerId = guidelineParamsMap.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID).getValue();
    }

    @Override
    public void onTrackedMarkerUpdate(MarkerDetection detection, double headRotation) {
        log.debug(String.format("tracked marker update: %s, at: %.2f[deg], distance: %.2f",
                detection.getId(),
                headRotation,
                ArucoMarkerUtils.distanceTo(detection)));

        boolean isCenteredOn = isCenteredOn(detection);

        if (currentStage == CompoundStrategyStage.LOOKING_AT_MARKER) {
            if (detection.getId().equals(MarkerDetection.EMPTY_DETECTION_ID)) {
                arUcoTracker.stop();
                currentStage = CompoundStrategyStage.LOOK_AROUND_FOR_MARKER;
                arUcoTracker.start();
            } else if (isCenteredOn && !isInRequestedPosition(detection, headRotation)) {
                System.out.printf("distance: %.2fm, angle %.2f°",
                        ArucoMarkerUtils.distanceTo(detection), headRotation);
                maneuverDescriptions = createManeuverDescriptions(detection, headRotation);
                currentStage = CompoundStrategyStage.MANEUVER;
                startManeuvers();
                return;
            }
        }

        if (currentStage == CompoundStrategyStage.LOOK_AROUND_FOR_MARKER) {
            if (detection.getId().equals(MarkerDetection.EMPTY_DETECTION_ID)) {
                finishedListener.onStrategyFinished(false);
                return;
            }

            if (detection.getId().equals(markerId) && isCenteredOn && isInRequestedPosition(detection, headRotation)) {
                // TODO - if in correct position - finish with success
                // else - retry the maneuver
                // if not found - failure
                finishedListener.onStrategyFinished(true);
            } else if (detection.getId().equals(markerId) && isCenteredOn) {
                System.out.printf("distance: %.2fm, angle %.2f°\n",
                        ArucoMarkerUtils.distanceTo(detection), headRotation);
                maneuverDescriptions = createManeuverDescriptions(detection, headRotation);
                currentStage = CompoundStrategyStage.MANEUVER;
                arUcoTracker.stop();
                startManeuvers();
            }
        }
        // TODO handle detection / switch stages or finish
    }

    private Queue<ManeuverDescription> createManeuverDescriptions(MarkerDetection detection, double headRotation) {

        maneuverDescriptions.clear();

        double srcX = detection.getCameraPosition()[0];
        double srcY = detection.getCameraPosition()[2];

        RelativeAlignment targetAlignment = RelativeAlignment
                .valueOf(guidelineParamsMap.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT)
                        .getValue()
                        .toUpperCase());
        RelativeDirection targetDirection = RelativeDirection
                .valueOf(guidelineParamsMap.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION)
                        .getValue()
                        .toUpperCase());

        double dstX = ManeuverUtils.relativeAlignmentToMeters(targetAlignment);
        double dstY = ACCORDING_TO_MARKER_DISTANCE;
        double dstRotation = ManeuverUtils.relativeDirectionToDegreesWithIgnoringOffset(targetDirection, dstX, dstY);

        Queue<ManeuverDescription> newManeuvers = maneuverGenerator
                .generateManeuverDescriptions(srcX, srcY, headRotation,
                        dstX, dstY, dstRotation);
        this.maneuverDescriptions.addAll(newManeuvers);

        return maneuverDescriptions;
    }

    private void startManeuvers() {
        if (maneuverDescriptions.isEmpty()) {
            log.error("Maneuver descriptions is empty");
            return;
        }

        ManeuverDescription description = maneuverDescriptions.poll();
        deadReckoningDrive.startManeuver(description.getName(), description.getRotationDegrees(), description.getDistanceMeters());
    }

    private boolean isCenteredOn(MarkerDetection detection) {
        double[] xCorners = detection.getXCorners();

        double averagePicturePositionPixels = (xCorners[0] + xCorners[1] + xCorners[2] + xCorners[3]) / 4.0 - CAM_PREVIEW_WIDTH / 2.0;    // 0 is the middle of the picture
        System.out.printf("average picture position [px]: %.2f\n", averagePicturePositionPixels);
        boolean isInPixelMargin = Math.abs(averagePicturePositionPixels) <= 5;

        if (isInPixelMargin && earliestCenteredOnTimeStamp == Long.MAX_VALUE) {
            earliestCenteredOnTimeStamp = System.currentTimeMillis();
            return false;
        }

        if (isInPixelMargin && isCenteringTimeElapsed(System.currentTimeMillis())) {
            earliestCenteredOnTimeStamp = Long.MAX_VALUE;
            return true;
        }
        return false;
    }

    private boolean isCenteringTimeElapsed(long currentTimeMillis) {
        return currentTimeMillis - earliestCenteredOnTimeStamp> MARKER_CENTERING_TIME_MS;
    }

    @Override
    public void onManeuverFinished(boolean isWithoutObstacles) {
        if (!isWithoutObstacles) {
            finishedListener.onStrategyFinished(false);
            return;
        } else if (maneuverDescriptions.isEmpty()){
            finishedListener.onStrategyFinished(true); // TODO check the position and retry the maneuvers
            return;
        }

        ManeuverDescription description = maneuverDescriptions.poll();
        deadReckoningDrive.startManeuver(description.getName(), description.getRotationDegrees(), description.getDistanceMeters());
    }

    private IDeadReckoningDrive initializeDeadReckoningDrive() {
        long fullRotationTimeMilliseconds = Long.parseLong(guidelineParamsMap.get(KEY_MANEUVER_ROBOT_FULL_ROTATION_MS).getValue());

        IDeadReckoningDrive deadReckoningDrive = new DeadReckoningDrive(CASE_WIDTH + WHEEL_WIDTH, WHEEL_DIAMETER, fullRotationTimeMilliseconds);
        deadReckoningDrive.setWheelsVelocitiesListener(wheelsVelocitiesChangeListener);
        deadReckoningDrive.setManeuverFinishListener(this);
        deadReckoningDrive.setWheelsParameters(CASE_WIDTH + WHEEL_WIDTH, WHEEL_DIAMETER, fullRotationTimeMilliseconds);
        return deadReckoningDrive;
    }

    private boolean isInRequestedPosition(MarkerDetection detection, double angle) {
        return detection.getId().equals(markerId)
                && detection.getRelativeDistance().equals(RelativeDistance.CLOSE)
                && detection.getRelativeAlignment().equals(requestedRelativeAlignment)
                && isChassisCorrectlyRotated(angle);

    }

    private boolean isChassisCorrectlyRotated(double angleDegrees) {
        return Math.abs(requestedRelativeDirection.getRotationDegrees() - angleDegrees) <= 5.0;
    }
}
