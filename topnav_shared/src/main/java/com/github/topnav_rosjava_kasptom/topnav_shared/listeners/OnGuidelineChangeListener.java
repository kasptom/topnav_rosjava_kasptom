package com.github.topnav_rosjava_kasptom.topnav_shared.listeners;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;

public interface OnGuidelineChangeListener {
    void onGuidelineChange(Guideline guideline);

    void onNoGuidelineAvailable();
}
