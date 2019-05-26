package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import topnav_msgs.HoughAcc;

public interface IReaction  {
    WheelsVelocities onHoughAccMessage(HoughAcc houghAcc);
}
