package com.github.topnav_rosjava_kasptom.topnav_navigator.view;

public interface IGuidelineView {
    void onShowOptions();
    void onSelectOption();
    void onAddParam(String name);
    void onRemoveParam(String name);

    void onSendGuideline();
}
