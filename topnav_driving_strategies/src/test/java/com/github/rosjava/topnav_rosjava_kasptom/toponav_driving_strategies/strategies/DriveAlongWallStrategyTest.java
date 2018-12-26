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
    void testKeepTargetAngle() {
        DriveAlongWallStrategy strategy = new DriveAlongWallStrategy(logMock);

        WheelsVelocities expectedVelocity = new WheelsVelocities(1.61, 2.39, 1.61, 2.31);

        WheelsVelocities wheelsVelocities = strategy.keepTargetAngle(235, 180);
        assertEquals(expectedVelocity.getFrontLeft(), wheelsVelocities.getFrontLeft());
        assertEquals(expectedVelocity.getFrontRight(), wheelsVelocities.getFrontRight());
        assertEquals(expectedVelocity.getRearLeft(), wheelsVelocities.getRearLeft());
        assertEquals(expectedVelocity.getRearRight(), wheelsVelocities.getRearRight());
    }

}