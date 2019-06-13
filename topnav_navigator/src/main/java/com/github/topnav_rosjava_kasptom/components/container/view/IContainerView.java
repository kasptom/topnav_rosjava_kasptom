package com.github.topnav_rosjava_kasptom.components.container.view;

import com.github.topnav_rosjava_kasptom.components.autopilot.view.IAutopilotView;
import com.github.topnav_rosjava_kasptom.components.feedback.view.IFeedbackView;
import com.github.topnav_rosjava_kasptom.components.remote_controller.view.IRemoteControlView;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.view.IGuidelineView;

public interface IContainerView {
    IGuidelineView getGuideLineView();
    IFeedbackView getFeedbackView();
    IAutopilotView getAutopilotView();
    IRemoteControlView getRemoteControlView();
}
