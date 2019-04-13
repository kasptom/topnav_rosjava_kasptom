package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;

import java.util.List;

public interface IFeedbackResolver {
    boolean shouldSwitchToNextGuideline(Feedback feedback, int currentGuidelineIdx, List<Guideline> guidelines);

    boolean shouldStop(Feedback feedback, int currentGuidelineIdx, List<Guideline> guidelines);
}
