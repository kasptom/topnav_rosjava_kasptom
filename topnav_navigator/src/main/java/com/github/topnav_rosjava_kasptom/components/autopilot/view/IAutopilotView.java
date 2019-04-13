package com.github.topnav_rosjava_kasptom.components.autopilot.view;

import com.github.topnav_rosjava_kasptom.components.IBaseView;

public interface IAutopilotView extends IBaseView {
    void setShowGraphButtonEnabled(boolean isEnabled);

    void setDisplayedRosonPath(String rosonPath);

    void setDisplayedGuideline(String guideline);

    String getStartMarkerId();

    String getEndMarkerId();

    void openGuidelinesWindow(String guidelines);
}
