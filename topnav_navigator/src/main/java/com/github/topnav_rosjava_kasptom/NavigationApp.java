package com.github.topnav_rosjava_kasptom;

import com.github.topnav_rosjava_kasptom.components.container.view.IContainerView;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationApp extends Application {
    private IGuidelinePresenter presenter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/view_container.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 640, 480);

        // FIXME DI
        presenter = ((IContainerView) loader.getController()).getGuideLineView().getPresenter();
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
