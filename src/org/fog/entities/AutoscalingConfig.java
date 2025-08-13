package org.fog.entities;

import java.util.HashMap;
import java.util.Map;

public class AutoscalingConfig {
    public static class ServiceConfig {
        public double cpuHigh = 0.75;
        public double cpuLow = 0.35;
        public double memHigh = 0.80;
        public double memLow = 0.40;
        public double latHighMs = -1; // negative = disabled

        public double tHighHoldSec = 15; // multiples of local tick
        public double tLowHoldSec = 40;

        public double cooldownOutSec = 10;
        public double cooldownInSec = 30;

        public int minReplicas = 1;
        public int maxReplicas = 8;
        public int stepOut = 1;
        public int stepIn = 1;
        public int forecastHorizonTicks = 3;
        public int predictorEveryNTicks = 2;
    }

    private final Map<String, ServiceConfig> serviceConfigs = new HashMap<>();

    public ServiceConfig forService(String svc) {
        return serviceConfigs.computeIfAbsent(svc, k -> new ServiceConfig());
    }
}


