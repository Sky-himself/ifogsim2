package org.fog.entities;

import java.util.ArrayList;
import java.util.List;

public class ClusterHeartbeat {
    public static class Capacity {
        public double cpuFree;
        public double ramFree;
        public int nodes;
    }

    public static class ServiceSummary {
        public String svc;
        public int replicas;
        public double cpuAvg;
        public double memAvg;
        public boolean canScaleOut;
        public boolean canScaleIn;
    }

    public double ts;
    public String cluster; // device name
    public Capacity capacity = new Capacity();
    public List<ServiceSummary> services = new ArrayList<>();
    public List<String> alerts = new ArrayList<>();
}


