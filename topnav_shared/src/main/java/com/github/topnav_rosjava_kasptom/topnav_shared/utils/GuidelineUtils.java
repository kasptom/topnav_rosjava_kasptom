package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDistance;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Topology;

import java.util.*;
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

    public static List<Topology> accordingToMarkersAsTopologies(HashMap<String, GuidelineParam> guidelineParams) {
        ArrayList<Topology> topologies = new ArrayList<>();

        if (guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_1)
                && guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_1)
                && guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_1)) {
            topologies.add(new Topology(0L, guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_1).getValue(),
                    guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_1).getValue(),
                    guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_1).getValue(), RelativeDistance.CLOSE.name()));
        }

        if (guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_2)
                && guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_2)
                && guidelineParams.containsKey(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_2)) {
            topologies.add(new Topology(0L, guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_MARKER_ID_2).getValue(),
                    guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_ALIGNMENT_2).getValue(),
                    guidelineParams.get(DrivingStrategy.PositionAccordingToMarker.KEY_ACCORDING_DIRECTION_2).getValue(), RelativeDistance.CLOSE.name()));
        }

        return topologies;
    }
}
