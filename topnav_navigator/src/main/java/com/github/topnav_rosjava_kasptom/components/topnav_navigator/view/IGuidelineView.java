package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.IBaseView;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;

import java.util.List;

public interface IGuidelineView extends IBaseView {
    void onShowOptions();

    void onSelectStrategy(String option);

    void onAddParam();

    void onRemoveParam();

    List<GuidelineParam> getGuidelineParams();

    void onSendGuideline();

    void onShowError(String format);
}
