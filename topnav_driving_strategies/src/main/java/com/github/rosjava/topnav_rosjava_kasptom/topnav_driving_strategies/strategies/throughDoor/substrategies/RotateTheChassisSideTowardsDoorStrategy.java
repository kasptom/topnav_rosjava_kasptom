package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopologyMsg;

import java.util.Arrays;
import java.util.List;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.ThroughDoorStage.ALIGN_BETWEEN_DOOR;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class RotateTheChassisSideTowardsDoorStrategy extends BaseSubStrategy {
    private final WheelsVelocitiesChangeListener wheelsListener;
    private final HeadRotationChangeListener headListener;
    private final SubStrategyListener subStrategyListener;
    private final StrategyFinishedListener finishListener;
    private List<RelativeDirection> directionsToCheck = Arrays.asList(AT_LEFT, AHEAD, AT_RIGHT, BEHIND);
    private int checkedDirection = 0;

    private boolean isChassisRotationInProgress;
    private Log log;

    public RotateTheChassisSideTowardsDoorStrategy(WheelsVelocitiesChangeListener wheelsListener,
                                                   HeadRotationChangeListener headListener,
                                                   SubStrategyListener substrategyListener,
                                                   StrategyFinishedListener finishListener, Log log) {
        this.wheelsListener = wheelsListener;
        this.headListener = headListener;
        this.subStrategyListener = substrategyListener;
        this.finishListener = finishListener;
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
        List<TopologyMsg> expectedDoorMarkers = PassThroughDoorUtils.findFrontDoorMarkers(feedbackMsg, guidelineParamHashMap);

        if (isChassisRotationInProgress) {
            if (expectedDoorMarkers.size() > 0) {
                wheelsListener.onWheelsVelocitiesChanged(WheelsVelocityConstants.ZERO_VELOCITY);
                log.info("rotated side towards the door");
                subStrategyListener.onStageChanged(ALIGN_BETWEEN_DOOR, AT_LEFT);
            } else {
                wheelsListener.onWheelsVelocitiesChanged(new WheelsVelocities(1.5, -1.5, 1.5, -1.5));
            }
            return;
        }

        if (expectedDoorMarkers.size() == 0) {
            checkedDirection++;
            subStrategyListener.headRotationInProgress(true);
        } else {
            isChassisRotationInProgress = true;
        }

        if (checkedDirection >= directionsToCheck.size()) {
            finishListener.onStrategyFinished(false);
        } else {
            headListener.onRotationChanged(directionsToCheck.get(checkedDirection));
        }
    }
}
