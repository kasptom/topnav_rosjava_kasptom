package com.github.topnav_rosjava_kasptom.components.autopilot.view;

import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.AutopilotPresenter;
import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.IAutopilotPresenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AutopilotView implements IAutopilotView, Initializable {

    @FXML
    public Button btnShowGraph;

    @FXML
    public TextArea txtAreaCurrentGuideline;

    @FXML
    public TextField txtFieldStartMarker;

    @FXML
    public TextField txtFieldEndMarker;

    @FXML
    public TextField txtAreaRosonFilePath;

    private IAutopilotPresenter presenter;

    public AutopilotView() {
        this.presenter = new AutopilotPresenter(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtAreaCurrentGuideline.setDisable(true);
        presenter.initializePresenter();
        setShowGraphButtonEnabled(false);
    }

    @Override
    public void setShowGraphButtonEnabled(boolean isEnabled) {
        btnShowGraph.setDisable(!isEnabled);
    }

    @Override
    public void setDisplayedRosonPath(String rosonPath) {
        txtAreaRosonFilePath.setText(rosonPath);
    }

    // FXML
    @FXML
    public void onLoadRosonClicked() {
        presenter.loadRoson();
    }

    @FXML
    public void onShowGraphClicked() {
        presenter.showGraph();
    }
}
