package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeRequestListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.StrategyFinishedListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.BaseSubStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.substrategies.SubStrategyListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;

import java.util.HashMap;

public class DetectMarkerSubStrategy extends BaseSubStrategy {
    private final Log log;

    public DetectMarkerSubStrategy(WheelsVelocitiesChangeListener wheelsListener,
                                   HeadRotationChangeRequestListener headListener,
                                   SubStrategyListener subStrategyListener,
                                   StrategyFinishedListener finishListener,
                                   Log log,
                                   HashMap<String, GuidelineParam> guidelineParamsMap) {
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
}
