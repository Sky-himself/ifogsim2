package org.fog.entities;

import java.util.ArrayDeque;
import java.util.Deque;

public class AutoscalingTelemetry {
    public static class Window {
        private final int capacity;
        private final Deque<Double> values = new ArrayDeque<>();
        private double sum = 0.0;

        public Window(int capacity) { this.capacity = capacity; }

        public void add(double v) {
            values.addLast(v);
            sum += v;
            if (values.size() > capacity) {
                sum -= values.removeFirst();
            }
        }

        public double avg() { return values.isEmpty() ? 0.0 : sum / values.size(); }
        public int size() { return values.size(); }
    }

    public final String service;
    public double cpu; // 0..1
    public double mem; // 0..1
    public double latP95Ms; // optional, -1 if unknown

    public AutoscalingTelemetry(String service, double cpu, double mem, double latP95Ms) {
        this.service = service; this.cpu = cpu; this.mem = mem; this.latP95Ms = latP95Ms;
    }
}


