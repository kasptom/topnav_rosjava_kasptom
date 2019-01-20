package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.strategies.throughDoor;

public interface BlockedMessageHandler<T> {
    void handle(T message);

    static <M> void handleIfNotBlocked(M message, BlockedMessageHandler<M> handler, boolean isBlocked) {
        if (!isBlocked) {
            handler.handle(message);
        }
    }
}
