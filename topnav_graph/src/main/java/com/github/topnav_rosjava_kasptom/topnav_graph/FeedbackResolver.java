package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Topology;

import java.util.List;

public class FeedbackResolver implements IFeedbackResolver {

    @Override
    public boolean shouldSwitchToNextGuideline(Feedback feedback, int currentGuidelineIdx, List<Guideline> guidelines) {
        if (currentGuidelineIdx >= guidelines.size() - 1) return false;

        List<Topology> topologies = feedback.getTopologies();
        Guideline currentGuideline = guidelines.get(currentGuidelineIdx);
        Guideline nextGuideline = guidelines.get(currentGuidelineIdx + 1);


//        if (nextGuideline.getGuidelineType().equals(DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2))

//        TODO executed strategy name in Feedback
        return false;
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
