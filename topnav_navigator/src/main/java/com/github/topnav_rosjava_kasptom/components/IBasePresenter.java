package com.github.topnav_rosjava_kasptom.components;

public interface IBasePresenter {
    void onInit() throws InterruptedException;

    void onDestroy();
}
