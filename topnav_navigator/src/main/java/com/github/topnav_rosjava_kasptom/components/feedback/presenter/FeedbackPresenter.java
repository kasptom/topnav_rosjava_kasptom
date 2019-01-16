package com.github.topnav_rosjava_kasptom.components.feedback.presenter;

import com.github.topnav_rosjava_kasptom.components.feedback.view.IFeedbackView;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;

import javax.annotation.PostConstruct;

public class FeedbackPresenter implements IFeedbackPresenter {

    private final IFeedbackView view;

    public FeedbackPresenter(IFeedbackView view) {
        this.view = view;
    }

    @PostConstruct
    private void init() {
//        this.service.setOnFeedbackChangeListener(this::onFeedbackReceived);
    }

    @Override
    public void onFeedbackReceived(Feedback feedback) {
    }
}
