package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuidelineUtils {
    public static void reloadParameters(List<String> guidelineParameters, HashMap<String, GuidelineParam> guidelineParamsMap) {
        List<GuidelineParam> paramToValue = guidelineParameters
                .stream()
                .map(parameter -> {
                    List<String> splitEntry = Arrays.stream(parameter.split(";"))
                            .map(String::trim)
                            .collect(Collectors.toList());

                    return new GuidelineParam(splitEntry.get(0), splitEntry.get(1), splitEntry.get(2));
                })
                .collect(Collectors.toList());
        guidelineParamsMap.clear();
        paramToValue.forEach(param -> guidelineParamsMap.put(param.getName(), param));
    }

    public static List<String> convertToStrings(List<GuidelineParam> guideLineParams) {
        return guideLineParams
                .stream()
                .map(guidelineParam -> String.format("%s;%s;%s",
                        guidelineParam.getName(),
                        guidelineParam.getValue(),
                        guidelineParam.getType()))
                .collect(Collectors.toList());
    }
}