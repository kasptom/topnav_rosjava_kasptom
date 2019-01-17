package com.github.topnav_rosjava_kasptom.components.feedback.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;

public interface IFeedbackPresenter extends IBasePresenter {
    void onFeedbackReceived(Feedback feedback);
}
