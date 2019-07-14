package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.maneuver;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.deadReckoning.ManeuverDescription;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.VALUE_MANEUVER_NAME_FORWARD;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DeadReckoning.VALUE_MANEUVER_NAME_ROTATE;
import static org.junit.jupiter.api.Assertions.*;

class ManeuverDescriptionGeneratorTest {
    private static final double MAX_ALLOWED_ANGLE_DELTA_DEGREES = 1e-3;
    private static final double MAX_ALLOWED_DISTANCE_DELTA_METERS = 1e-3;

    @Test
    void sourceCoordinatesA_generateDescriptions_expectedManeuvers() {
        double srcX = -1.0, srcY = 1.0, srcRotation = 45.0;
        double dstX = 1.0, dstY = 1.0, dstRotation = 135.0;
        IManeuverDescriptionGenerator generator = new ManeuverDescriptionGenerator();
        int expectedManeuversCount = 3;

        Queue<ManeuverDescription> descriptions = generator.generateManeuverDescriptions(srcX, srcY, srcRotation, dstX, dstY, dstRotation);

        assertEquals(expectedManeuversCount, descriptions.size());

        ManeuverDescription first = descriptions.poll();
        assertNotNull(first);
        assertEquals(0.0, first.getDistanceMeters(), MAX_ALLOWED_DISTANCE_DELTA_METERS);
        assertEquals(VALUE_MANEUVER_NAME_ROTATE, first.getName());
        assertEquals(0.0, first.getRotationDegrees(), MAX_ALLOWED_ANGLE_DELTA_DEGREES);
    }

    @Test
    void sourceCoordinatesB_generateDescriptions_expectedManeuvers() {
        double srcX = 1.0, srcY = 1.0, srcRotation = 135.0;
        double dstX = -1.0, dstY = 1.0, dstRotation = 45.0;
        IManeuverDescriptionGenerator generator = new ManeuverDescriptionGenerator();
        int expectedManeuversCount = 3;

        Queue<ManeuverDescription> descriptions = generator.generateManeuverDescriptions(srcX, srcY, srcRotation, dstX, dstY, dstRotation);

        assertEquals(expectedManeuversCount, descriptions.size());

        ManeuverDescription first = descriptions.poll();
        assertNotNull(first);
        assertEquals(0.0, first.getDistanceMeters(), MAX_ALLOWED_DISTANCE_DELTA_METERS);
        assertEquals(VALUE_MANEUVER_NAME_ROTATE, first.getName());
        assertEquals(180.0, first.getRotationDegrees(), MAX_ALLOWED_ANGLE_DELTA_DEGREES);
    }

    @Test
    void sourceCoordinatesC_generateDescriptions_expectedManeuvers() {
        double srcX = -1.0, srcY = 1.0, srcRotation = 90.0;
        double dstX = 1.0, dstY = 1.0, dstRotation = 135.0;
        IManeuverDescriptionGenerator generator = new ManeuverDescriptionGenerator();
        int expectedManeuversCount = 3;

        Queue<ManeuverDescription> descriptions = generator.generateManeuverDescriptions(srcX, srcY, srcRotation, dstX, dstY, dstRotation);

        assertEquals(expectedManeuversCount, descriptions.size());

        ManeuverDescription first = descriptions.poll();
        assertNotNull(first);
        assertEquals(0.0, first.getDistanceMeters(), MAX_ALLOWED_DISTANCE_DELTA_METERS);
        assertEquals(VALUE_MANEUVER_NAME_ROTATE, first.getName());
        assertEquals(45.0, first.getRotationDegrees(), MAX_ALLOWED_ANGLE_DELTA_DEGREES);
    }
}
