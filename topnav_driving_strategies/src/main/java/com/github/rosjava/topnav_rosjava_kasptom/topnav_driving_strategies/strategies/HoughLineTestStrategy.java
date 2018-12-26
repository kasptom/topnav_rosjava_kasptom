package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsController;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.WheelsVelocitiesChangeListener;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils.HoughUtils;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.HoughCell;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;
import topnav_msgs.HoughAcc;
import topnav_msgs.TopNavConfigMsg;

import java.util.List;
import java.util.stream.Collectors;

public class HoughLineTestStrategy implements WheelsController.IDrivingStrategy {

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
        HoughCell bestLine = filteredHoughCells.stream().max(HoughCell::compareTo).orElse(null);
        if (bestLine == null) {
            return ZERO_VELOCITY;
        }

        double angle = (bestLine.getAngleDegrees() + 90) % 360;
        double range = bestLine.getRange();

        log.debug(String.format("best line: %.2f[°], %.2f[m]", angle, range));

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
