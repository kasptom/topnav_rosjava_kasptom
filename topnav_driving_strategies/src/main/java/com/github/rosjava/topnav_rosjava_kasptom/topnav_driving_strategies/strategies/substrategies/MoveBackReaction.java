package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.substrategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.reactions.IReaction;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.PdVelocityCalculator;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.HoughUtils;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.MOVE_BACK_VELOCITY;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.WheelsVelocityConstants.ZERO_VELOCITY;

public class MoveBackReaction implements IReaction {
    private PdVelocityCalculator velocityCalculator = PdVelocityCalculator.createDefaultPdVelocityCalculator();

    @Override
    public WheelsVelocities onHoughAccMessage(HoughAcc houghAcc) {
        int lineDetectionThreshold = 8;
        List<HoughCell> filteredHoughCells = HoughUtils.toFilteredList(houghAcc, lineDetectionThreshold);
        return moveBack(filteredHoughCells);
    }

    @Override
    public WheelsVelocities onAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        List<AngleRange> angleRanges = AngleRange.messageToAngleRange(angleRangesMsg);
        angleRanges = angleRanges.stream()
                .filter(angleRange -> angleRange.getRange() <= SAVE_RANGE)
                .collect(Collectors.toList());
        AngleRange minAngleRange = angleRanges
                .stream()
                .min(Comparator.comparingDouble(AngleRange::getRange))
                .orElse(null);

        if (minAngleRange == null) {
            return ZERO_VELOCITY;
        }

        WheelsVelocities rotationVelocityComponent = computeBackRotationComponent(minAngleRange);

        return WheelsVelocities.addVelocities(MOVE_BACK_VELOCITY, rotationVelocityComponent);
    }

    private WheelsVelocities moveBack(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells.stream()
                .min(Comparator.comparingDouble(HoughCell::getRange))
                .orElse(null);

        if (bestLine == null || bestLine.getRange() > 2 * TOO_CLOSE_RANGE) {
            return ZERO_VELOCITY;
        }

        WheelsVelocities rotationVelocityComponent = computeBackRotationComponent(bestLine);

        return WheelsVelocities.addVelocities(MOVE_BACK_VELOCITY, rotationVelocityComponent);
    }

    private WheelsVelocities computeBackRotationComponent(HoughCell bestLine) {
        return velocityCalculator.calculateRotationSpeed(
                bestLine.getAngleDegreesLidarDomain(),
                bestLine.getRange(),
                System.nanoTime(),
                AHEAD_OBSTACLE_ANGLE,
                TARGET_WALL_RANGE);
    }

    private WheelsVelocities computeBackRotationComponent(AngleRange angleRange) {
        return velocityCalculator.calculateRotationSpeed(
                angleRange.getAngleRad(),
                angleRange.getRange(),
                System.nanoTime(),
                AHEAD_OBSTACLE_ANGLE,
                TARGET_WALL_RANGE
        );
    }
}
