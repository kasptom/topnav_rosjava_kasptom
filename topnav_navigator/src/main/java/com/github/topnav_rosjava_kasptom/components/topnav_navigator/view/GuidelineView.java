package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.GuidelinePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class GuidelineView implements IGuidelineView{

    private final GuidelinePresenter presenter;

    @FXML
    public TextField commandInput;

    @FXML
    public Button buttonStartStrategy;
    public Button buttonStopStrategy;

    public GuidelineView() {
        this.presenter = new GuidelinePresenter(this);
    }

    @FXML
    public void onStartStrategy() {
        presenter.onStartStrategy();
    }

    @Override
    public IGuidelinePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onShowOptions() {

    }

    @Override
    public void onSelectOption() {

    }

    @Override
    public void onAddParam(String name) {

    }

    @Override
    public void onRemoveParam(String name) {

    }

    @Override
    public void onSendGuideline() {

    }

    @Override
    public void onShowError(String format) {

    }
}
