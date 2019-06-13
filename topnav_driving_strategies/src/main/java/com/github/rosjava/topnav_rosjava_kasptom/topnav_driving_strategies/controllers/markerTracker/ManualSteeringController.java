package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers.markerTracker;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.Limits;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.WheelsVelocities;
import com.github.topnav_rosjava_kasptom.topnav_shared.utils.SteeringKeyDecoder;

public class ManualSteeringController implements IManualSteering {
    @Override
    public WheelsVelocities handleSteeringMessage(short decodedSteering) {
        WheelsVelocities velocities = new WheelsVelocities(0.0, 0.0, 0.0, 0.0);
        boolean[] chosenDirections = SteeringKeyDecoder.decode(decodedSteering);
        if (chosenDirections[0]) { // front
            velocities = new WheelsVelocities(velocities.getFrontLeft() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getFrontRight() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearLeft() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearRight() + Limits.MAX_VELOCITY_DELTA);
        }

        if (chosenDirections[1]) { // back
            velocities = new WheelsVelocities(velocities.getFrontLeft() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getFrontRight() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearLeft() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearRight() - Limits.MAX_VELOCITY_DELTA);
        }

        if (chosenDirections[2]) { // lefft
            velocities = new WheelsVelocities(velocities.getFrontLeft() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getFrontRight() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearLeft() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearRight() + Limits.MAX_VELOCITY_DELTA);
        }

        if (chosenDirections[3]) { // right
            velocities = new WheelsVelocities(velocities.getFrontLeft() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getFrontRight() - Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearLeft() + Limits.MAX_VELOCITY_DELTA,
                    velocities.getRearRight() - Limits.MAX_VELOCITY_DELTA);
        }
        return velocities;
    }
}
