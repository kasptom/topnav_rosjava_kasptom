package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.DriveAlongWallStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@Deprecated
public class DriveAlongWallStrategyTest {
    @Mock
    Log logMock;


    @Test
    public void lowerAngle_testKeepTargetAngle_leftSpinsFaster() {
        DriveAlongWallStrategy strategy = new DriveAlongWallStrategy(logMock);

        WheelsVelocities expectedVelocity = new WheelsVelocities(2.39, 1.61, 2.39, 1.61);

        WheelsVelocities wheelsVelocities = strategy.keepTargetAngle(305, 270);
        assertEquals(expectedVelocity.getFrontLeft(), wheelsVelocities.getFrontLeft(), 0.01);
        assertEquals(expectedVelocity.getFrontRight(), wheelsVelocities.getFrontRight(), 0.01);
        assertEquals(expectedVelocity.getRearLeft(), wheelsVelocities.getRearLeft(), 0.01);
        assertEquals(expectedVelocity.getRearRight(), wheelsVelocities.getRearRight(), 0.01);
    }

    @Test
    public void lowerAngle_testKeepTargetAngle_rightSpinsFaster() {
        DriveAlongWallStrategy strategy = new DriveAlongWallStrategy(logMock);

        WheelsVelocities expectedVelocity = new WheelsVelocities(1.61, 2.39, 1.61, 2.39);

        WheelsVelocities wheelsVelocities = strategy.keepTargetAngle(235, 270);
        assertEquals(expectedVelocity.getFrontLeft(), wheelsVelocities.getFrontLeft(), 0.01);
        assertEquals(expectedVelocity.getFrontRight(), wheelsVelocities.getFrontRight(), 0.01);
        assertEquals(expectedVelocity.getRearLeft(), wheelsVelocities.getRearLeft(), 0.01);
        assertEquals(expectedVelocity.getRearRight(), wheelsVelocities.getRearRight(), 0.01);
    }
}