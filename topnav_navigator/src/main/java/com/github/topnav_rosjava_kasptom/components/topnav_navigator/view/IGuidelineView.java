package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;

public interface IGuidelineView {
    IGuidelinePresenter getPresenter();

    void onShowOptions();
    void onSelectOption();
    void onAddParam(String name);
    void onRemoveParam(String name);

    void onSendGuideline();

    void onShowError(String format);
}
