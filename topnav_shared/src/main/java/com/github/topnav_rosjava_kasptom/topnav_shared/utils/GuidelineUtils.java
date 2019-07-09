package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_LEFT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_RIGHT;

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

    public static LinkedHashSet<String> asOrderedDoorMarkerIds(HashMap<String, GuidelineParam> guidelineParams) {
        LinkedHashSet<String> doorMarkersOrdered = new LinkedHashSet<>(4);
        doorMarkersOrdered.add(guidelineParams.getOrDefault(DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        doorMarkersOrdered.add(guidelineParams.getOrDefault(DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        doorMarkersOrdered.add(guidelineParams.getOrDefault(DrivingStrategy.ThroughDoor.KEY_BACK_LEFT_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        doorMarkersOrdered.add(guidelineParams.getOrDefault(DrivingStrategy.ThroughDoor.KEY_BACK_RIGHT_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        return doorMarkersOrdered;
    }

    public static LinkedHashSet<String> approachedMarkerIdAsSet(HashMap<String, GuidelineParam> guidelineParams) {
        LinkedHashSet<String> approachedMarkerSet = new LinkedHashSet<>(1);
        approachedMarkerSet.add(guidelineParams.getOrDefault(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        return approachedMarkerSet;
    }

    public static LinkedHashSet<String> accordingToMarkerIdAsSet(HashMap<String, GuidelineParam> guidelineParams) {
        LinkedHashSet<String> approachedMarkerSet = new LinkedHashSet<>(1);
        approachedMarkerSet.add(guidelineParams.getOrDefault(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID, GuidelineParam.getEmptyParam()).getValue());
        return approachedMarkerSet;
    }
}
