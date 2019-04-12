package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.MARKER_PARAMS;

public class FeedbackResolver implements IFeedbackResolver {

    @Override
    public boolean shouldSwitchToNextGuideline(Feedback feedback, int currentGuidelineIdx, List<Guideline> guidelines) {
        if (currentGuidelineIdx >= guidelines.size() - 1) return false;

        if (DrivingStrategy.DRIVING_STRATEGY_IDLE.equals(feedback.getStrategyName())) return true;

        if (!DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2.equals(feedback.getStrategyName())) return false;

        Guideline nextGuideline = guidelines.get(currentGuidelineIdx + 1);
        HashSet<String> nextGuidelineTopologies = nextGuideline.getParameters()
                .stream()
                .filter(param -> MARKER_PARAMS.contains(param.getName()))
                .map(GuidelineParam::getValue)
                .collect(Collectors.toCollection(HashSet::new));

        List<String> visibleNextGuidelineTopologies = feedback.getTopologies()
                .stream()
                .filter(topology -> nextGuidelineTopologies.contains(topology.getIdentity()))
                .map(Topology::getIdentity)
                .collect(Collectors.toList());

        return visibleNextGuidelineTopologies.size() > 0;
    }

    @Override
    public boolean shouldStop(Feedback feedback, int currentGuidelineIdx, List<Guideline> guidelines) {
        if (currentGuidelineIdx >= guidelines.size() - 1) return true;

        Guideline currentGuideline = guidelines.get(currentGuidelineIdx);
        Guideline nextGuideline = guidelines.get(currentGuidelineIdx + 1);

//        if ()
        return false; // TODO close to the unexpected marker
    }
}
