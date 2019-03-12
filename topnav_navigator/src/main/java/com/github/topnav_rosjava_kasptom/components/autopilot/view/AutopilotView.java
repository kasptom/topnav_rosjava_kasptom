package com.github.topnav_rosjava_kasptom.components.autopilot.view;

import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.AutopilotPresenter;
import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.IAutopilotPresenter;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AutopilotView implements IAutopilotView, Initializable {

    public TextArea txtAreaCurrentGuideline;
    private IAutopilotPresenter presenter;

    public TextField txtFieldStartMarker;
    public TextField txtFieldEndMarker;

    public AutopilotView() {
        this.presenter = new AutopilotPresenter(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtAreaCurrentGuideline.setDisable(true);
    }
}
