package com.github.topnav_rosjava_kasptom;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.container.view.IContainerView;
import com.github.topnav_rosjava_kasptom.components.feedback.presenter.IFeedbackPresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationApp extends Application {
    private IBasePresenter guidelinePresenter;
    private IBasePresenter feedbackPresenter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/view_container.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 720, 640);

        initPresenters(loader);

        primaryStage.setTitle("Navigation app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.guidelinePresenter.onDestroy();
        this.feedbackPresenter.onDestroy();
        super.stop();
    }

    private void initPresenters(FXMLLoader loader) throws InterruptedException {
        // FIXME DI
        guidelinePresenter = ((IContainerView) loader.getController()).getGuideLineView().getPresenter();
        guidelinePresenter.onInit();

        feedbackPresenter = ((IContainerView) loader.getController()).getFeedbackView().getPresenter();
        feedbackPresenter.onInit();
    }
}
