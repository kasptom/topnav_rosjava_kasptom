package com.github.rosjava.topnav_rosjava_kasptom.toponav_driving_strategies.strategies;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.WheelsVelocities;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;


class DriveAlongWallStrategyTest {
    @Mock
    Log logMock;


    @Test
    void lowerAngle_testKeepTargetAngle_leftSpinsFaster() {
        DriveAlongWallStrategy strategy = new DriveAlongWallStrategy(logMock);

        WheelsVelocities expectedVelocity = new WheelsVelocities(2.39, 1.61, 2.39, 1.61);

        WheelsVelocities wheelsVelocities = strategy.keepTargetAngle(305, 270);
        assertEquals(expectedVelocity.getFrontLeft(), wheelsVelocities.getFrontLeft(), 0.01);
        assertEquals(expectedVelocity.getFrontRight(), wheelsVelocities.getFrontRight(), 0.01);
        assertEquals(expectedVelocity.getRearLeft(), wheelsVelocities.getRearLeft(), 0.01);
        assertEquals(expectedVelocity.getRearRight(), wheelsVelocities.getRearRight(), 0.01);
    }

    @Test
    void lowerAngle_testKeepTargetAngle_rightSpinsFaster() {
        DriveAlongWallStrategy strategy = new DriveAlongWallStrategy(logMock);

        WheelsVelocities expectedVelocity = new WheelsVelocities(1.61, 2.39, 1.61, 2.39);

        WheelsVelocities wheelsVelocities = strategy.keepTargetAngle(235, 270);
        assertEquals(expectedVelocity.getFrontLeft(), wheelsVelocities.getFrontLeft(), 0.01);
        assertEquals(expectedVelocity.getFrontRight(), wheelsVelocities.getFrontRight(), 0.01);
        assertEquals(expectedVelocity.getRearLeft(), wheelsVelocities.getRearLeft(), 0.01);
        assertEquals(expectedVelocity.getRearRight(), wheelsVelocities.getRearRight(), 0.01);
    }
}