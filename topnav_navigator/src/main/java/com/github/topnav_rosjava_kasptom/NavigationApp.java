package com.github.topnav_rosjava_kasptom;

import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.view.IGuidelineView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationApp extends Application {
    private IGuidelinePresenter presenter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/view_guideline.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 640, 480);

        presenter = ((IGuidelineView)loader.getController()).getPresenter();
        presenter.onInit();

        primaryStage.setTitle("Navigation app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.presenter.onDestroy();
        super.stop();
    }
}
