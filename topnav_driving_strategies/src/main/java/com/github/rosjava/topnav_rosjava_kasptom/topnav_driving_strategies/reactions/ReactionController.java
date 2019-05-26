package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.MoveBackReaction;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import topnav_msgs.HoughAcc;

public class ReactionController implements IReactionController, IReactionStartListener {
    private final Log log;
    private WheelsVelocitiesChangeListener wheelsListener;

    private IReaction reaction = new MoveBackReaction();    // TODO HashMap
    private boolean isReactionInProgress;

    public ReactionController(ConnectedNode node) {
        this.log = node.getLog();
    }

    @Override
    public boolean isReactionInProgress() {
        return isReactionInProgress;
    }

    @Override
    public void onHoughAccMessage(HoughAcc houghAcc) {
        if (!isReactionInProgress) {
            return;
        }

        WheelsVelocities wheelsVelocities = reaction.onHoughAccMessage(houghAcc);
        if (wheelsVelocities == WheelsVelocityConstants.ZERO_VELOCITY) {
            isReactionInProgress = false;
        }

        wheelsListener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void setWheelsVelocitiesLIstener(WheelsVelocitiesChangeListener wheelsListener) {
        this.wheelsListener = wheelsListener;
    }

    @Override
    public void onReactionStart(String reactionName) {
        log.info(String.format("Starting reaction: %s", reactionName));
        isReactionInProgress = true;
    }
}
