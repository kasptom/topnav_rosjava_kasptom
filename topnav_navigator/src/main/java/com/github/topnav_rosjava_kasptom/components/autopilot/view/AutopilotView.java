package com.github.topnav_rosjava_kasptom.components.autopilot.view;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.AutopilotPresenter;
import com.github.topnav_rosjava_kasptom.components.autopilot.presenter.IAutopilotPresenter;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

    @FXML
    public Button btnShowGuidelines;

    private IAutopilotPresenter presenter;

    public AutopilotView() {
        this.presenter = new AutopilotPresenter(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.initializePresenter();
    }

    @Override
    public void setShowGraphButtonEnabled(boolean isEnabled) {
        btnShowGraph.setDisable(!isEnabled);
    }

    @Override
    public void setDisplayedRosonPath(String rosonPath) {
        txtAreaRosonFilePath.setText(rosonPath);
    }

    @Override
    public void setDisplayedGuideline(String guideline) {
        txtAreaCurrentGuideline.setText(guideline);
    }

    @Override
    public String getStartMarkerId() {
        return txtFieldStartMarker.getText();
    }

    @Override
    public String getEndMarkerId() {
        return txtFieldEndMarker.getText();
    }

    @Override
    public void openGuidelinesWindow(String guidelines) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(700);
        alert.setTitle("Guidelines");
        alert.setHeaderText("");
        alert.setContentText(guidelines);
        alert.showAndWait();
    }

    @Override
    public IBasePresenter getPresenter() {
        return presenter;
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

    @FXML
    public void play() throws InvalidArUcoIdException {
        presenter.play();
    }

    @FXML
    public void pause() {
        presenter.pause();
    }

    @FXML
    public void stop() {
        presenter.stop();
    }

    @FXML
    public void onShowAllGuidelines() {
        presenter.showAllGuidelines();
    }
}
