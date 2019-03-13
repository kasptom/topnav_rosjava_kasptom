package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IArUcoHeadTracker;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.BaseSubStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.SubStrategyListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.ThroughDoorStage;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;

import java.util.HashMap;

public class DetectMarkerSubStrategy extends BaseSubStrategy implements IArUcoHeadTracker.TrackedMarkerListener {
    private final IArUcoHeadTracker arUcoTracker;
    private final Log log;

    public DetectMarkerSubStrategy(IArUcoHeadTracker arUcoTracker, WheelsVelocitiesChangeListener wheelsListener,
                                   HeadRotationChangeRequestListener headListener,
                                   SubStrategyListener subStrategyListener,
                                   StrategyFinishedListener finishListener,
                                   Log log,
                                   HashMap<String, GuidelineParam> guidelineParamsMap) {
        super(wheelsListener, headListener, subStrategyListener, finishListener, guidelineParamsMap);
        this.arUcoTracker = arUcoTracker;
        this.log = log;
    }

    @Override
    public void startStrategy() {
        super.startStrategy();
        arUcoTracker.start();
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
        if (detection.getId().equals(MarkerDetection.EMPTY_DETECTION_ID)) {
            finishListener.onStrategyFinished(false);
        } else {
            subStrategyListener.onStageFinished(ThroughDoorStage.DETECT_MARKER, RelativeDirection.UNDEFINED);
        }
    }
}
