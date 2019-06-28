package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;

public interface IReaction  {
    WheelsVelocities onHoughAccMessage(HoughAcc houghAcc);

    /**
     * Fallback method when no line was detected in the 'too close range'
     * @param angleRangesMsg - LIDAR detecion point
     * @return wheels velocities
     */
    WheelsVelocities onAngleRangeMessage(AngleRangesMsg angleRangesMsg);
}
