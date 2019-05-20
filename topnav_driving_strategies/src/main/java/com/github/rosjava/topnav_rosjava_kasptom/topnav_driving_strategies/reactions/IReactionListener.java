package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

public interface IReactionListener {
    void onStartReaction(String reactionName);

    void onStopReaction();
}
