package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.HeadRotationChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.GuidelineUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.HashMap;
import java.util.List;

import static com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor.PassThroughDoorStrategy.ThroughDoorStage.*;

public class PassThroughDoorStrategy implements IDrivingStrategy {
    private final Log log;

    private HeadRotationChangeListener headListener;
    private WheelsVelocitiesChangeListener wheelsListener;

    private HashMap<String, GuidelineParam> guidelineParamsMap;
    private HashMap<ThroughDoorStage, IDrivingStrategy> substrategies;
    private ThroughDoorStage currentStage;

    public PassThroughDoorStrategy(Log log) {
        this.log = log;
        this.currentStage = DETECTED_MARKER;
        substrategies = initializeSubstrategies();
    }

    private HashMap<ThroughDoorStage, IDrivingStrategy> initializeSubstrategies() {
        this.substrategies = new HashMap<>();
        this.substrategies.put(DETECTED_MARKER, new RotateTheChassisSideTowardsDoorStrategy());
        this.substrategies.put(ROTATED_SIDE_TOWARDS_DOOR, new AlignBetweenDoorMarkersStrategy());
        this.substrategies.put(ALIGNED_WITH_DOOR, new RotateTheChassisFrontTowardsDoorStrategy());
        this.substrategies.put(ROTATED_TOWARDS_DOOR, new DriveThroughDoorStrategy());
        return substrategies;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        this.substrategies.get(currentStage).handleConfigMessage(configMsg);
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        this.substrategies.get(currentStage).handleHoughAccMessage(houghAcc);
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        this.substrategies.get(currentStage).handleAngleRangeMessage(angleRangesMsg);
    }

    @Override
    public void handleDetectionMessage(FeedbackMsg feedbackMsg) {
        this.substrategies.get(currentStage).handleDetectionMessage(feedbackMsg);
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        wheelsListener = listener;
    }

    @Override
    public void setHeadRotationChangeListener(HeadRotationChangeListener listener) {
        headListener = listener;
    }

    @Override
    public void setGuidelineParameters(List<String> guidelineParameters) {
        GuidelineUtils.reloadParameters(guidelineParameters, guidelineParamsMap);
    }

    enum ThroughDoorStage {
        DETECTED_MARKER,
        ROTATED_SIDE_TOWARDS_DOOR,
        ALIGNED_WITH_DOOR,
        ROTATED_TOWARDS_DOOR,
        BACK_MARKER_SPOTTED,
    }

    abstract class BaseThroughDoorSubStrategy implements IDrivingStrategy {

        @Override
        public void handleConfigMessage(TopNavConfigMsg configMsg) { }

        @Override
        public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) { }

        @Override
        public void setHeadRotationChangeListener(HeadRotationChangeListener listener) { }

        @Override
        public void setGuidelineParameters(List<String> parameters) { }
    }


    class RotateTheChassisSideTowardsDoorStrategy extends BaseThroughDoorSubStrategy {

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

    class AlignBetweenDoorMarkersStrategy extends BaseThroughDoorSubStrategy {

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

    class RotateTheChassisFrontTowardsDoorStrategy extends BaseThroughDoorSubStrategy {

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

    class DriveThroughDoorStrategy extends BaseThroughDoorSubStrategy {

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
}
