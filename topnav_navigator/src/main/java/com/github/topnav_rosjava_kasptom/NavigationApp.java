package com.github.topnav_rosjava_kasptom;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.container.view.IContainerView;
import com.github.topnav_rosjava_kasptom.components.remote_controller.IRemoteControlCommandSender;
import com.github.topnav_rosjava_kasptom.components.remote_controller.KeyPressHandler;
import com.github.topnav_rosjava_kasptom.components.remote_controller.KeyReleaseHandler;
import com.github.topnav_rosjava_kasptom.components.remote_controller.RemoteControlCommandSender;
import com.github.topnav_rosjava_kasptom.components.remote_controller.presenter.IRemoteControlPresenter;
import com.github.topnav_rosjava_kasptom.services.PropertiesService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class NavigationApp extends Application {
    private IBasePresenter guidelinePresenter;
    private IBasePresenter feedbackPresenter;
    private IBasePresenter autopilotPresenter;
    private IBasePresenter controllerPresenter;
    private IRemoteControlCommandSender commandSender;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!getParameters().getRaw().isEmpty()) {
            String configFilePath = getParameters().getRaw().get(0);
            PropertiesService.getInstance(configFilePath);
        } else {
            PropertiesService.getInstance(null);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/view_container.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 720, 640);

        initCommandSender(scene);
        initPresenters(loader);

        primaryStage.setTitle("Navigation app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.guidelinePresenter.onDestroy();
        this.feedbackPresenter.onDestroy();
        this.autopilotPresenter.onDestroy();
        this.controllerPresenter.onDestroy();
        super.stop();
    }

    private void initPresenters(FXMLLoader loader) throws InterruptedException {
        // FIXME DI
        guidelinePresenter = ((IContainerView) loader.getController()).getGuideLineView().getPresenter();
        guidelinePresenter.onInit();

        feedbackPresenter = ((IContainerView) loader.getController()).getFeedbackView().getPresenter();
        feedbackPresenter.onInit();

        autopilotPresenter = ((IContainerView) loader.getController()).getAutopilotView().getPresenter();
        autopilotPresenter.onInit();

        controllerPresenter = ((IContainerView) loader.getController()).getRemoteControlView().getPresenter();
        controllerPresenter.onInit();
        ((IRemoteControlPresenter)controllerPresenter).setOnControlCheckboxChangedListener((IRemoteControlPresenter.OnControlCheckboxChangedListener) commandSender);
    }

    private void initCommandSender(Scene scene) {
        commandSender = new RemoteControlCommandSender();
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new KeyPressHandler(commandSender));
        scene.addEventHandler(KeyEvent.KEY_RELEASED, new KeyReleaseHandler(commandSender));
    }
}
