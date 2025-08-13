package org.fog.entities;

import java.util.ArrayDeque;
import java.util.Deque;

public class AutoscalingPredictor {
    private final double alpha;
    public AutoscalingPredictor(double alpha) { this.alpha = alpha; }

    private double es(Deque<Double> series) {
        if (series.isEmpty()) return 0;
        double level = series.peekFirst();
        for (Double x : series) {
            level = alpha * x + (1 - alpha) * level;
        }
        return level;
    }

    public double predict(Deque<Double> series, int horizon) {
        // Simple ES: horizon ignored for simplicity
        return es(series);
    }
}


