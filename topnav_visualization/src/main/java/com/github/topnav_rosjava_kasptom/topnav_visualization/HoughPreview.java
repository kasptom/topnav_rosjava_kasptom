package com.github.topnav_rosjava_kasptom.topnav_visualization;

import sensor_msgs.LaserScan;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_HEIGHT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.PREVIEW_WIDTH;

public class HoughPreview extends Frame implements IHoughPreview {

    private ArrayList<Point> points;


    public HoughPreview() {
        super("Java 2D HoughPreview");

        points = new ArrayList<>();

        setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }


    @Override
    public void paint(Graphics g) {
        points.forEach(point -> {
            g.setColor(Color.red);
            g.drawRect((int) point.x, (int) point.y, 5, 5);
        });
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.setColor(Color.blue);
//        g2d.drawRect(75, 75, 300, 200);
    }

    @Override
    public void onLaserPointsUpdated(LaserScan scanMsg) {
        points.clear();
        double x, y, angle, range;
        for (int i = 0; i < scanMsg.getRanges().length; i++) {
            angle = scanMsg.getAngleMin() + scanMsg.getAngleIncrement() * i;
            range = scanMsg.getRanges()[i];

            x = (range * Math.cos(angle) - scanMsg.getRangeMin()) / (scanMsg.getRangeMax() - scanMsg.getRangeMin()) * PREVIEW_WIDTH / 2;
            y = (range * Math.sin(angle) - scanMsg.getRangeMin()) / (scanMsg.getRangeMax() - scanMsg.getRangeMin()) * PREVIEW_HEIGHT / 2;

            x += PREVIEW_WIDTH / 2.0;
            y += PREVIEW_HEIGHT / 2.0;

            points.add(new Point(x, y));
        }

//        SwingUtilities.invokeLater(() -> pause(500));
        repaint(50);
//        pause(500);
    }

    class Point {
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        double x;
        double y;
    }
}