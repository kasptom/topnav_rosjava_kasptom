package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import topnav_msgs.HoughAcc;

public interface IReactionController {
    boolean isReactionInProgress();

    void onHoughAccMessage(HoughAcc houghAcc);

    void setWheelsVelocitiesLIstener(WheelsVelocitiesChangeListener wheelsListener);
}
