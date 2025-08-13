package org.fog.entities;

import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.utils.FogEvents;
import org.fog.application.AppModule;

import java.util.HashMap;
import java.util.Map;

public class AutoscalerCO {
    private final int deviceId;
    private final AutoscalingConfig config;
    private final AutoscalingState state = new AutoscalingState();
    private final AutoscalingPredictor predictor = new AutoscalingPredictor(0.3);

    // service -> holds satisfied counters (in ticks)
    private final Map<String, Integer> highHoldTicks = new HashMap<>();
    private final Map<String, Integer> lowHoldTicks = new HashMap<>();

    // local tick seconds
    private final double deltaT;

    public AutoscalerCO(int deviceId, AutoscalingConfig config, double deltaTSeconds) {
        this.deviceId = deviceId;
        this.config = config;
        this.deltaT = deltaTSeconds;
    }

    public void onTelemetry(AutoscalingTelemetry t) {
        AutoscalingState.ServiceState st = state.forService(t.service);
        st.cpuHist.addLast(t.cpu);
        st.memHist.addLast(t.mem);
        if (t.latP95Ms >= 0) st.latHist.addLast(t.latP95Ms);
        // cap history size roughly to 50
        while (st.cpuHist.size() > 50) st.cpuHist.removeFirst();
        while (st.memHist.size() > 50) st.memHist.removeFirst();
        while (st.latHist.size() > 50) st.latHist.removeFirst();

        stepReactive(t.service);
        stepPredictive(t.service);
    }

    private int currentReplicas(String svc, MicroserviceFogDevice device) {
        Map<String, Map<String, Integer>> mic = device.getModuleInstanceCount();
        int total = 0;
        for (Map<String, Integer> perApp : mic.values()) {
            total += perApp.getOrDefault(svc, 0);
        }
        return total;
    }

    private boolean hasLocalCapacity(MicroserviceFogDevice device, String svc, int k, AppModule template) {
        double mipsNeeded = template.getMips() * k;
        double ramNeeded = template.getRam() * k;
        double storageNeeded = template.getSize() * k;
        ControllerComponent cc = device.getControllerComponent();
        Double cpuFree = cc.getAvailableResource(device.getId(), ControllerComponent.CPU);
        Double ramFree = cc.getAvailableResource(device.getId(), ControllerComponent.RAM);
        Double storageFree = cc.getAvailableResource(device.getId(), ControllerComponent.STORAGE);
        return cpuFree != null && ramFree != null && storageFree != null &&
                cpuFree >= mipsNeeded && ramFree >= ramNeeded && storageFree >= storageNeeded;
    }

    private void stepReactive(String svc) {
        AutoscalingConfig.ServiceConfig sc = config.forService(svc);
        AutoscalingState.ServiceState st = state.forService(svc);
        double now = CloudSim.clock();
        if (now < st.nextAllowedActionTime) return;

        double cpuAvg = st.cpuHist.stream().mapToDouble(d -> d).average().orElse(0);
        double memAvg = st.memHist.stream().mapToDouble(d -> d).average().orElse(0);
        double latAvg = st.latHist.stream().mapToDouble(d -> d).average().orElse(-1);

        boolean highBreach = cpuAvg >= sc.cpuHigh || memAvg >= sc.memHigh || (sc.latHighMs > 0 && latAvg >= sc.latHighMs);
        boolean lowOk = cpuAvg <= sc.cpuLow && memAvg <= sc.memLow && (sc.latHighMs <= 0 || latAvg < 0.8 * sc.latHighMs);

        int h = highHoldTicks.getOrDefault(svc, 0);
        int l = lowHoldTicks.getOrDefault(svc, 0);
        h = highBreach ? h + 1 : 0;
        l = lowOk ? l + 1 : 0;
        highHoldTicks.put(svc, h);
        lowHoldTicks.put(svc, l);

        int ticksToHigh = (int) Math.max(1, Math.round(sc.tHighHoldSec / deltaT));
        int ticksToLow = (int) Math.max(1, Math.round(sc.tLowHoldSec / deltaT));

        if (h >= ticksToHigh) {
            proposeScaleOut(svc, sc.stepOut, sc.cooldownOutSec);
            highHoldTicks.put(svc, 0);
        } else if (l >= ticksToLow) {
            proposeScaleIn(svc, sc.stepIn, sc.cooldownInSec);
            lowHoldTicks.put(svc, 0);
        }
    }

    private void stepPredictive(String svc) {
        AutoscalingConfig.ServiceConfig sc = config.forService(svc);
        AutoscalingState.ServiceState st = state.forService(svc);
        double now = CloudSim.clock();
        if (now < st.nextAllowedActionTime) return;

        double cpuHat = predictor.predict(st.cpuHist, sc.forecastHorizonTicks);
        double memHat = predictor.predict(st.memHist, sc.forecastHorizonTicks);
        double latHat = st.latHist.isEmpty() ? -1 : predictor.predict(st.latHist, sc.forecastHorizonTicks);

        boolean breach = cpuHat >= sc.cpuHigh || memHat >= sc.memHigh || (sc.latHighMs > 0 && latHat >= sc.latHighMs);
        if (breach) {
            proposeScaleOut(svc, sc.stepOut, sc.cooldownOutSec);
        }
    }

    private void proposeScaleOut(String svc, int step, double cooldownSec) {
        AutoscalingState.ServiceState st = state.forService(svc);
        st.phase = AutoscalingState.Phase.COOLING_OUT;
        st.nextAllowedActionTime = CloudSim.clock() + cooldownSec;
        // Send proposal to device self: positive instances
        ScaleDecision d = new ScaleDecision(svc, step);
        CloudSim.send(deviceId, deviceId, 0, FogEvents.AUTOSCALE_TICK, d);
    }

    private void proposeScaleIn(String svc, int step, double cooldownSec) {
        AutoscalingState.ServiceState st = state.forService(svc);
        st.phase = AutoscalingState.Phase.COOLING_IN;
        st.nextAllowedActionTime = CloudSim.clock() + cooldownSec;
        ScaleDecision d = new ScaleDecision(svc, -step);
        CloudSim.send(deviceId, deviceId, 0, FogEvents.AUTOSCALE_TICK, d);
    }

    public static class ScaleDecision {
        public final String service;
        public final int deltaReplicas; // positive = out, negative = in
        public ScaleDecision(String service, int deltaReplicas) { this.service = service; this.deltaReplicas = deltaReplicas; }
    }
}


