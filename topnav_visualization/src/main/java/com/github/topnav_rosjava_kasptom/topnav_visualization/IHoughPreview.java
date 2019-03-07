package com.github.topnav_rosjava_kasptom.topnav_visualization;

import topnav_msgs.AngleRangesMsg;

public interface IHoughPreview {
    void onAngleRangeMessage(AngleRangesMsg angleRangesMsg);
}
