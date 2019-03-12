package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import com.github.topnav_rosjava_kasptom.components.autopilot.view.IAutopilotView;
import com.github.topnav_rosjava_kasptom.services.IRosTopnavService;
import com.github.topnav_rosjava_kasptom.services.OnFeedbackChangeListener;
import com.github.topnav_rosjava_kasptom.services.RosTopNavService;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;

public class AutopilotPresenter implements IAutopilotPresenter, OnFeedbackChangeListener {
    private final IRosTopnavService rosService;
    private final IAutopilotView autopilotView;

    public AutopilotPresenter(IAutopilotView autopilotView) {
        this.autopilotView = autopilotView;
        rosService = RosTopNavService.getInstance();
    }

    @Override
    public void initializePresenter() {
        rosService.addOnFeedbackChangeListener(this);
    }

    @Override
    public void onFeedbackChange(Feedback feedback) {

    }
}
