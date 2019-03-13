package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopologyMsg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.CompoundStrategyStage.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ROTATE_CLOCKWISE_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection.*;

public class RotateTheChassisSideTowardsDoorStrategy extends BaseSubStrategy {
    private List<RelativeDirection> directionsToCheck = Arrays.asList(AT_LEFT, AHEAD, AT_RIGHT, BEHIND);
    private int checkedDirection = 0;

    private boolean isChassisRotationInProgress;
    private Log log;

    public RotateTheChassisSideTowardsDoorStrategy(WheelsVelocitiesChangeListener wheelsListener,
                                                   HeadRotationChangeRequestListener headListener,
                                                   SubStrategyListener substrategyListener,
                                                   StrategyFinishedListener finishListener, Log log, HashMap<String, GuidelineParam> guidelineParamsMap) {
        super(wheelsListener, headListener, substrategyListener, finishListener, guidelineParamsMap);
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
        List<TopologyMsg> expectedDoorMarkers = PassThroughDoorUtils.findFrontDoorMarkers(feedbackMsg, guidelineParamsMap);

        if (isChassisRotationInProgress) {
            if (expectedDoorMarkers.size() > 0) {
                wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
                log.info("rotated side towards the door");
                subStrategyListener.onStageFinished(DETECT_MARKER, AT_LEFT);
            } else {
                wheelsListener.onWheelsVelocitiesChanged(ROTATE_CLOCKWISE_VELOCITY);
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
            headListener.onRotationChangeRequest(directionsToCheck.get(checkedDirection));
        }
    }
}
