package com.github.topnav_rosjava_kasptom.components.container.view;

import com.github.topnav_rosjava_kasptom.components.feedback.view.IFeedbackView;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.view.IGuidelineView;
import javafx.fxml.FXML;

public class ContainerView implements IContainerView {

    @FXML
    @SuppressWarnings("unused")
    private IGuidelineView guidelineController;

    @FXML
    private IFeedbackView feedbackView;

    @Override
    public IGuidelineView getGuideLineView() {
        return guidelineController;
    }

    @Override
    public IFeedbackView getFeedbackView() {
        return feedbackView;
    }
}
