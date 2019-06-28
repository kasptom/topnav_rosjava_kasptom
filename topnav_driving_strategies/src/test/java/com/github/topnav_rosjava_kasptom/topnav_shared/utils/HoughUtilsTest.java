package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.HoughCell;
import com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder.DoorFinder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HoughUtilsTest {

    private double angleDelta = 0.0001;

    @Test
    void pointsAB1_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(5.0, -5.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(5.0, 5.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( -90.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsAB2_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(0.0, 10.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(5.0, 5.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( -30.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsAB3_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(0.0, 10.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(-5.0, 5.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( 30.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsAB4_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(-5.0, 5.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(-5.0, -5.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( 90.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsAB5_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(-5.0, -5.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(0.0, -10.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( 150.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsAB6_toHoughCell_returnsAngleAndRange() {
        DoorFinder.Point a = new DoorFinder.Point(0.0, -10.0 / Math.sqrt(3.0));
        DoorFinder.Point b = new DoorFinder.Point(5.0, -5.0 / Math.sqrt(3.0));

        HoughCell cell = HoughUtils.toHoughCell(a, b);

        assertEquals( -150.0, cell.getAngleDegreesLidarDomain(), angleDelta);
    }

    @Test
    void pointsDifferentOrder_toHoughCell_noChangeInValueAndrange() {
        DoorFinder.Point a1 = new DoorFinder.Point(5.0, -5.0 / Math.sqrt(3.0));
        DoorFinder.Point b1 = new DoorFinder.Point(5.0, 5.0 / Math.sqrt(3.0));

        DoorFinder.Point a2 = new DoorFinder.Point(0.0, 10.0 / Math.sqrt(3.0));
        DoorFinder.Point b2 = new DoorFinder.Point(5.0, 5.0 / Math.sqrt(3.0));

        DoorFinder.Point a3 = new DoorFinder.Point(0.0, 10.0 / Math.sqrt(3.0));
        DoorFinder.Point b3 = new DoorFinder.Point(-5.0, 5.0 / Math.sqrt(3.0));

        DoorFinder.Point a4 = new DoorFinder.Point(-5.0, 5.0 / Math.sqrt(3.0));
        DoorFinder.Point b4 = new DoorFinder.Point(-5.0, -5.0 / Math.sqrt(3.0));

        DoorFinder.Point a5 = new DoorFinder.Point(-5.0, 5.0 / Math.sqrt(3.0));
        DoorFinder.Point b5 = new DoorFinder.Point(0.0, -10.0 / Math.sqrt(3.0));

        DoorFinder.Point a6 = new DoorFinder.Point(0.0, -10.0 / Math.sqrt(3.0));
        DoorFinder.Point b6 = new DoorFinder.Point(5.0, -5.0 / Math.sqrt(3.0));

        HoughCell cell11 = HoughUtils.toHoughCell(a1, b1);
        HoughCell cell12 = HoughUtils.toHoughCell(b1, a1);

        HoughCell cell21 = HoughUtils.toHoughCell(a2, b2);
        HoughCell cell22 = HoughUtils.toHoughCell(b2, a2);

        HoughCell cell31 = HoughUtils.toHoughCell(a3, b3);
        HoughCell cell32 = HoughUtils.toHoughCell(b3, a3);

        HoughCell cell41 = HoughUtils.toHoughCell(a4, b4);
        HoughCell cell42 = HoughUtils.toHoughCell(b4, a4);

        HoughCell cell51 = HoughUtils.toHoughCell(a5, b5);
        HoughCell cell52 = HoughUtils.toHoughCell(b5, a5);

        HoughCell cell61 = HoughUtils.toHoughCell(a6, b6);
        HoughCell cell62 = HoughUtils.toHoughCell(b6, a6);


        assertEquals(cell11.getRange(), cell12.getRange());
        assertEquals(cell11.getAngleDegreesLidarDomain(), cell12.getAngleDegreesLidarDomain());

        assertEquals(cell21.getRange(), cell22.getRange());
        assertEquals(cell21.getAngleDegreesLidarDomain(), cell22.getAngleDegreesLidarDomain());

        assertEquals(cell31.getRange(), cell32.getRange());
        assertEquals(cell31.getAngleDegreesLidarDomain(), cell32.getAngleDegreesLidarDomain());

        assertEquals(cell41.getRange(), cell42.getRange());
        assertEquals(cell41.getAngleDegreesLidarDomain(), cell42.getAngleDegreesLidarDomain());

        assertEquals(cell51.getRange(), cell52.getRange());
        assertEquals(cell51.getAngleDegreesLidarDomain(), cell52.getAngleDegreesLidarDomain());

        assertEquals(cell61.getRange(), cell62.getRange());
        assertEquals(cell61.getAngleDegreesLidarDomain(), cell62.getAngleDegreesLidarDomain());
    }
}