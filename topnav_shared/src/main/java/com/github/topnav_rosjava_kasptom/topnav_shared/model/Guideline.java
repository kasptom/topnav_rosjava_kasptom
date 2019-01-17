package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import java.util.List;

public class Guideline {
    private final String guidelineType;
    private final List<GuidelineParam> parameters;

    public Guideline(String guidelineType, List<GuidelineParam> parameters) {
        this.guidelineType = guidelineType;
        this.parameters = parameters;
    }

    public String getGuidelineType() {
        return guidelineType;
    }

    public List<GuidelineParam> getParameters() {
        return parameters;
    }
}
