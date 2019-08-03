package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies.MoveBackReaction;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.AngleRangeUtils;
import org.apache.commons.logging.Log;
import org.ros.node.ConnectedNode;
import topnav_msgs.AngleRangesMsg;

import java.util.Arrays;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.OBSTACLE_TOO_CLOSE_LIMIT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.TOO_CLOSE_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class ReactionController implements IReactionController, IReactionStartListener {
    private final Log log;
    private final IReactionFinishListener reactionFinishListener;
    private WheelsVelocitiesChangeListener wheelsListener;

    private IReaction reaction = new MoveBackReaction();    // TODO HashMap
    private boolean isReactionInProgress;

    private boolean isObstacleTooClose;
    private int tooCloseCounter;

    public ReactionController(ConnectedNode node, IReactionFinishListener reactionFinishListener) {
        this.log = node.getLog();
        this.reactionFinishListener = reactionFinishListener;
    }

    @Override
    public boolean isReactionInProgress() {
        return isReactionInProgress;
    }

    @Override
    public boolean checkIfObstacleIsTooClose(AngleRangesMsg message) {

        isObstacleTooClose = Arrays.stream(message.getDistances()).anyMatch(dist -> dist <= TOO_CLOSE_RANGE);

        tooCloseCounter = isObstacleTooClose ? tooCloseCounter++ : 0;

        isReactionInProgress = tooCloseCounter > OBSTACLE_TOO_CLOSE_LIMIT;

        if (isReactionInProgress) {
            log.info("Obstacle is too close. Starting the reaction");
            AngleRangeUtils.printClosestPointInfo(message);
        }
        return isReactionInProgress;
    }

    @Override
    public void stopReaction() {
        log.info("Stopping the reaction");
        wheelsListener.onWheelsVelocitiesChanged(ZERO_VELOCITY);
        isReactionInProgress = false;
    }

    @Override
    public void onAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        if (!isReactionInProgress) {
            return;
        }

        WheelsVelocities wheelsVelocities = reaction.onAngleRangeMessage(angleRangesMsg);
        if (wheelsVelocities == ZERO_VELOCITY) {
            tooCloseCounter = 0;
            isReactionInProgress = false;
            reactionFinishListener.onReactionFinished();
            return;
        }

        wheelsListener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener wheelsListener) {
        this.wheelsListener = wheelsListener;
    }

    @Override
    public void onReactionStart(String reactionName) {
        log.info(String.format("Starting reaction: %s", reactionName));
        isReactionInProgress = true;
    }
}
