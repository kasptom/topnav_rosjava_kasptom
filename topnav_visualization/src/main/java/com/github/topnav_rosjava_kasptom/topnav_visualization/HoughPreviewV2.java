package com.github.topnav_rosjava_kasptom.topnav_visualization;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.AngleRange;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.DoorFinder;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.AngleRangeUtils;
import org.apache.commons.logging.Log;
import topnav_msgs.AngleRangesMsg;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.LIDAR_PREVIEW_HEIGHT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.Preview.LIDAR_PREVIEW_WIDTH;

public class HoughPreviewV2 implements IHoughPreview {
    private final Log log;

    private final DoorFinder doorFinder;
    private GraphicsDevice graphicsDevice;
    private Frame mainFrame;

    private ArrayList<Point> points;
    private ArrayList<Point> leftDoorPoints;
    private ArrayList<Point> rightDoorPoints;


    private static final long PREVIEW_UPDATE_INTERVAL_NANO_SECS = (long) (0.1 * 1e9);
    private long lastTimeStamp;
    private Point2D midPoint;


    public HoughPreviewV2(Log log) {
        this.log = log;
        this.lastTimeStamp = System.nanoTime();
        this.doorFinder = new DoorFinder();
        points = new ArrayList<>();
        leftDoorPoints = new ArrayList<>();
        rightDoorPoints = new ArrayList<>();
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
        leftDoorPoints.clear();
        rightDoorPoints.clear();

        ArrayList<Point2D> lidarPoints = AngleRangeUtils.angleRangeToPixels(angleRangesMsg);
        List<List<AngleRange>> clusters = doorFinder.dividePointsToClusters(angleRangesMsg);

        DoorFinder.Point midPoint = doorFinder.getClustersMidPoint();
        this.midPoint = AngleRangeUtils.pointToPixelPoint(midPoint);

        points.addAll(lidarPoints.stream()
                .map(point2D -> new Point((int) point2D.getX(), (int) point2D.getY()))
                .collect(Collectors.toList()));

        if (clusters.size() == 2) {
            leftDoorPoints.addAll(AngleRangeUtils.angleRangeToPixels(clusters.get(0))
                    .stream().map(point2D -> new Point((int) point2D.getX(), (int) point2D.getY()))
                    .collect(Collectors.toList()));
            rightDoorPoints.addAll(AngleRangeUtils.angleRangeToPixels(clusters.get(1))
                    .stream().map(point2D -> new Point((int) point2D.getX(), (int) point2D.getY()))
                    .collect(Collectors.toList()));
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
        graphics.fillRect(0, 0, LIDAR_PREVIEW_WIDTH, LIDAR_PREVIEW_HEIGHT);

        graphics.setColor(Color.red);
        drawPoints(graphics, points);

        graphics.setColor(Color.green);
        drawPoints(graphics, leftDoorPoints);

        graphics.setColor(Color.blue);
        drawPoints(graphics, rightDoorPoints);

        graphics.setColor(Color.yellow);
        graphics.fillRect((int)midPoint.getX(), (int)midPoint.getY(), 5, 5);

        graphics.dispose();
    }

    private void drawPoints(Graphics graphics, ArrayList<Point> points) {
        points.forEach(point -> graphics.fillRect(point.x, point.y, 5, 5));
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
        frame.setSize(LIDAR_PREVIEW_WIDTH, LIDAR_PREVIEW_HEIGHT);
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
