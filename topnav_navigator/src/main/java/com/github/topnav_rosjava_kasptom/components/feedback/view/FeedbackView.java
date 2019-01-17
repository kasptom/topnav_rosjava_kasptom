package com.github.topnav_rosjava_kasptom.components.feedback.view;

import com.github.topnav_rosjava_kasptom.components.feedback.presenter.FeedbackPresenter;
import com.github.topnav_rosjava_kasptom.components.feedback.presenter.IFeedbackPresenter;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FeedbackView implements IFeedbackView, Initializable {

    private final IFeedbackPresenter presenter;
    public ListView<String> listDetections;

    public FeedbackView() {
        this.presenter = new FeedbackPresenter(this);
    }

    @Override
    public IFeedbackPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onUpdateDetections(List<String> detectionStrings) {
        listDetections.getItems().clear();
        listDetections.getItems().addAll(detectionStrings);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
