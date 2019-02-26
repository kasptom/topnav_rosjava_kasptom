package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import topnav_msgs.AngleRangesMsg;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.LIDAR_MAX_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_HEIGHT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_WIDTH;

public class AngleRangeUtils {
    public static ArrayList<Point2D> angleRangeToPixels(AngleRangesMsg angleRangesMsg) {

        ArrayList<Point2D> points = new ArrayList<>();

        double x, y, angleRad, range;
        for (int i = 0; i < angleRangesMsg.getAngles().length; i++) {
            angleRad = angleRangesMsg.getAngles()[i];
            range = angleRangesMsg.getDistances()[i];

            if (range > LIDAR_MAX_RANGE) continue;

            x = range * Math.sin(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_HEIGHT / 2.0f);
            y = range * Math.cos(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_WIDTH / 2.0f);

            x = -x + PREVIEW_WIDTH / 2.0;
            y = -y + PREVIEW_HEIGHT / 2.0;

            points.add(new Point2D.Double(x, y));
        }

        return points;
    }

    public static ArrayList<Point2D> angleRangeToPixels(List<AngleRange> angleRanges) {
        return angleRanges.stream()
                .map(angleRange -> {
                    double angleRad = angleRange.getAngleRad();
                    double range = angleRange.getRange();

                    double x = range * Math.sin(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_HEIGHT / 2.0f);
                    double y = range * Math.cos(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_WIDTH / 2.0f);

                    x = -x + PREVIEW_WIDTH / 2.0;
                    y = -y + PREVIEW_HEIGHT / 2.0;
                    return new Point2D.Double(x, y);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
