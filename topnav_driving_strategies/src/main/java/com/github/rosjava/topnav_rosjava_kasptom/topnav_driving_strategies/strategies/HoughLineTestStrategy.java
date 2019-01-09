package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.IDrivingStrategy;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.HoughCell;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils.HoughUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HoughLineTestStrategy implements IDrivingStrategy {

    private final Log log;
    private static final WheelsVelocities ZERO_VELOCITY = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);

    private WheelsVelocitiesChangeListener listener;

    private int lineDetectionThreshold = 5;

    private int messageCounter = 0;
    private long timeStamp;

    public HoughLineTestStrategy(Log log) {
        this.log = log;
    }

    @Override
    public void handleConfigMessage(TopNavConfigMsg configMsg) {
        log.info("received config message");
        this.lineDetectionThreshold = configMsg.getLineDetectionThreshold();
        log.info(String.format("line detection threshold changed to: %d", lineDetectionThreshold));
    }

    @Override
    public void handleHoughAccMessage(HoughAcc houghAcc) {
        refreshRateCheck();

        List<HoughCell> houghCells = HoughUtils.toList(houghAcc);

        houghCells = houghCells
                .stream()
                .filter(cell -> cell.getVotes() >= this.lineDetectionThreshold)
                .collect(Collectors.toList());

        houghCells.forEach(cell -> log.debug(String.format(
                "detected line: (dst, ang, vts) = (%.2f %.2f [°], %d)",
                cell.getRange(), cell.getAngleDegrees(), cell.getVotes())));

        WheelsVelocities wheelsVelocities = logStatistics(houghCells);
        listener.onWheelsVelocitiesChanged(wheelsVelocities);
    }

    private int counter = 0;
    private double minBest = 400.0;
    private double maxBest = -400.0;

    private WheelsVelocities logStatistics(List<HoughCell> filteredHoughCells) {
        HoughCell bestLine = filteredHoughCells
                .stream()
                .min(Comparator.comparingDouble(HoughCell::getRange)).orElse(null);

        if (bestLine == null) {
            return ZERO_VELOCITY;
        }

        double angle = (bestLine.getAngleDegrees() + 90) % 360;
        double range = bestLine.getRange();

        log.info(String.format("best line: %.2f[°], %.2f[m]", angle, range));

        if (counter != 100) {
            counter++;
            minBest = angle < minBest ? angle : minBest;
            maxBest = angle > maxBest ? angle : maxBest;
        } else {
            log.info(String.format("Min best: %.2f[°], max best: %.2f[°]", minBest, maxBest));
            counter = 0;
            minBest = 400.0;
            maxBest = -400.0;
        }

        return ZERO_VELOCITY;
    }

    @Override
    public void handleAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        double minRange = Double.POSITIVE_INFINITY;
        double angle = -1000;
        double angleStep = 240.0 / (angleRangesMsg.getAngles().length - 1);

        for (int i = 0; i < angleRangesMsg.getAngles().length; i++) {
            if (minRange > angleRangesMsg.getDistances()[i]) {
                minRange = angleRangesMsg.getDistances()[i];
                angle = i * angleStep;
            }
        }

        log.info(String.format("Angle / range: min range: %.2f at angle %.2f[°]", minRange, angle));
    }

    @Override
    public void setWheelsVelocitiesListener(WheelsVelocitiesChangeListener listener) {
        this.listener = listener;
    }

    private void refreshRateCheck() {
        if (messageCounter == 0) {
            timeStamp = System.nanoTime();
        }

        this.messageCounter++;

        if (messageCounter == 40) {
            String timeInfo = String.format("received 40 messages within approx: %.3f [s]", (System.nanoTime() - timeStamp) / 1e9);
            log.info(timeInfo);
            messageCounter = 0;
        }
    }

}
