package com.github.topnav_rosjava_kasptom.components.feedback.view;

import com.github.topnav_rosjava_kasptom.components.IBaseView;

import java.util.List;

public interface IFeedbackView extends IBaseView {
    void onUpdateDetections(List<String> detectionStrings);
}
