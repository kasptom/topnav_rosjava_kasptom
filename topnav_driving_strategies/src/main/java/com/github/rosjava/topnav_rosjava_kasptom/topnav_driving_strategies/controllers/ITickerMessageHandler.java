package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

public interface ITickerMessageHandler {
    void handleTickerMessage(std_msgs.UInt64 tickerMsg);
}
