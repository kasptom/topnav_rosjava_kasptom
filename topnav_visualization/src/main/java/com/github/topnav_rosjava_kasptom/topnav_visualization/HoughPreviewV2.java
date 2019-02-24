package com.github.topnav_rosjava_kasptom.topnav_visualization;

import org.apache.commons.logging.Log;
import sensor_msgs.LaserScan;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_HEIGHT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_WIDTH;

public class HoughPreviewV2 implements IHoughPreview {
    private final Log log;
    GraphicsDevice graphicsDevice;
    Frame mainFrame;

    private ArrayList<Point> points;


    public HoughPreviewV2(Log log) {
        this.log = log;
        points = new ArrayList<>();
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
    public void onLaserPointsUpdated(LaserScan scanMsg) {
        points.clear();
        double x, y, angle, range;
        for (int i = 0; i < scanMsg.getRanges().length; i++) {
            angle = scanMsg.getAngleMin() + scanMsg.getAngleIncrement() * i;
            range = scanMsg.getRanges()[i];

            x = (range * Math.sin(angle) - scanMsg.getRangeMin()) / (scanMsg.getRangeMax() - scanMsg.getRangeMin()) * PREVIEW_HEIGHT / 2;
            y = (range * Math.cos(angle) - scanMsg.getRangeMin()) / (scanMsg.getRangeMax() - scanMsg.getRangeMin()) * PREVIEW_WIDTH / 2;

            x = -x + PREVIEW_WIDTH / 2.0;
            y = -y + PREVIEW_HEIGHT / 2.0;

            points.add(new Point((int) x, (int) y));
        }

        renderPoints();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            log.error("Thread interrupted");
        }
    }

    private void renderPoints() {
        Graphics graphics = mainFrame.getGraphics();

        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);

        graphics.setColor(Color.red);
        points.forEach(point -> {
            graphics.fillRect(point.x, point.y, 5, 5);

        });
        // Draw as appropriate using myGraphics
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
        Frame frame = new Frame(graphicsDevice.getDefaultConfiguration());
        frame.setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        return frame;
    }
}
