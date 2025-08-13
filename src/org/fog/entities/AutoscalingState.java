package org.fog.entities;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class AutoscalingState {
    public enum Phase { IDLE, COOLING_OUT, COOLING_IN }

    public static class ServiceState {
        public Phase phase = Phase.IDLE;
        public double nextAllowedActionTime = 0;
        public Deque<Double> cpuHist = new ArrayDeque<>();
        public Deque<Double> memHist = new ArrayDeque<>();
        public Deque<Double> latHist = new ArrayDeque<>();
        public double lastScaleDecisionTime = -1;
    }

    private final Map<String, ServiceState> perService = new HashMap<>();

    public ServiceState forService(String svc) { return perService.computeIfAbsent(svc, k -> new ServiceState()); }
}


