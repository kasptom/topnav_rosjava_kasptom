package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import java.util.List;

public class Guideline {
    private final String guidelineType;
    private final List<GuidelineParam> parameters;

    /**
     * @param guidelineType one of the {@link com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy}
     * @param parameters    parameters passed with the {@link com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy}
     */
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

    @Override
    public String toString() {
        return String.format("{ type: %s, parameters: %s }", guidelineType, parametersToString());
    }

    private String parametersToString() {
        return String.format("[%s]", parameters
                .stream()
                .map(GuidelineParam::toString)
                .reduce((params, param) -> String.format("%s, %s", params, param)));
    }
}
