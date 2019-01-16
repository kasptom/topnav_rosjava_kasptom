package com.github.topnav_rosjava_kasptom.components.feedback.presenter;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;

interface IFeedbackPresenter {
    void onFeedbackReceived(Feedback feedback);
}
