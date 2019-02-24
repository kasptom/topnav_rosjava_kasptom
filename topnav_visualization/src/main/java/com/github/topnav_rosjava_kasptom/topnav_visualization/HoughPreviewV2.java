package com.github.topnav_rosjava_kasptom.topnav_visualization;

import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits.LIDAR_MAX_RANGE;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_HEIGHT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_WIDTH;

public class HoughPreviewV2 implements IHoughPreview {
    private final Log log;
    private GraphicsDevice graphicsDevice;
    private Frame mainFrame;

    private ArrayList<Point> points;
    private ArrayList<Point> doorPoints;

    private static final long PREVIEW_UPDATE_INTERVAL_NANO_SECS = (long) (0.1 * 1e9);
    private long lastTimeStamp;


    public HoughPreviewV2(Log log) {
        this.log = log;
        this.lastTimeStamp = System.nanoTime();
        points = new ArrayList<>();
        doorPoints = new ArrayList<>();
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        printDeviceCapabilities();
        mainFrame = createMainFrame();
        try {
            graphicsDevice.setFullScreenWindow(mainFrame);
        } finally {
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    @Override
    public void onAngleRangeMessage(AngleRangesMsg angleRangesMsg) {
        if (!isTimeToUpdate()) {
            return;
        }

        updatePointsFromAngleRangeData(angleRangesMsg);

        renderPoints();
    }

    private void updatePointsFromAngleRangeData(AngleRangesMsg angleRangesMsg) {
        points.clear();
        double x, y, angleRad, range;
        for (int i = 0; i < angleRangesMsg.getAngles().length; i++) {
            angleRad= angleRangesMsg.getAngles()[i];
            range = angleRangesMsg.getDistances()[i];

            x = range * Math.sin(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_HEIGHT / 2.0f);
            y = range * Math.cos(angleRad) / LIDAR_MAX_RANGE * (PREVIEW_WIDTH / 2.0f);

            x = -x + PREVIEW_WIDTH / 2.0;
            y = -y + PREVIEW_HEIGHT / 2.0;

            points.add(new Point((int) x, (int) y));
        }
    }

    private boolean isTimeToUpdate() {
        long currentTimeStamp = System.nanoTime();
        if (currentTimeStamp - lastTimeStamp > PREVIEW_UPDATE_INTERVAL_NANO_SECS) {
//            log.info("update time");
            lastTimeStamp = currentTimeStamp;
            return true;
        }
        return false;
    }

    private void renderPoints() {
        Graphics graphics = mainFrame.getGraphics();

        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

        graphics.setColor(Color.red);
        points.forEach(point -> {
            graphics.fillRect(point.x, point.y, 5, 5);

        });
        graphics.setColor(Color.green);
        doorPoints.forEach(point -> {
            graphics.fillOval(point.x, point.y, 5, 5);
        });

        graphics.dispose();
    }

    private void printDeviceCapabilities() {
        if (graphicsDevice.isFullScreenSupported()) {
            System.out.println("full screen supported");
        } else {
            System.out.println("full screen not supported");
        }

        if (graphicsDevice.isDisplayChangeSupported()) {
            System.out.println("display change supported");
        } else {
            System.out.println("display change not supported");
        }
    }

    private Frame createMainFrame() {
        Frame frame = new Frame("LIDAR preview (java)", graphicsDevice.getDefaultConfiguration());
        frame.setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
//        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        return frame;
    }
}
