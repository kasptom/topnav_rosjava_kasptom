package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;

public interface IReactionController {
    boolean isReactionInProgress();

    void onAngleRangeMessage(AngleRangesMsg angleRangesMsg);

    void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener wheelsListener);

    boolean checkIfObstacleIsTooClose(AngleRangesMsg message);

    void stopReaction();

    interface IReactionFinishListener {
        void onReactionFinished();
    }
}
