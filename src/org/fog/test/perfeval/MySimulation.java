package org.fog.test.perfeval;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.entities.FogDevice;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.cloudbus.cloudsim.provisioners.*;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;

import java.util.*;

public class MySimulation {

    // === Global Variables ===
    static int numOfFogDevices = 5;   // number of fog nodes
    static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
    static Map<String, Integer> getIdByName = new HashMap<String, Integer>();

    public static void main(String[] args) {
        Log.printLine("Starting MySimulation...");

        try {
            // Step 1: Initialize CloudSim
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);

            // Step 2: Create Fog devices
            createFogDevices();

            // Step 3: No sensors/actuators for now (empty lists)
            List<org.fog.entities.Sensor> sensors = new ArrayList<>();
            List<org.fog.entities.Actuator> actuators = new ArrayList<>();

            // Step 4: Create Controller
            Controller controller = new Controller("master-controller", fogDevices, sensors, actuators);

            // ModuleMapping required by Controller (empty for now)
            ModuleMapping moduleMapping = ModuleMapping.createModuleMapping();
            controller.submitApplication(null, 0, null);

            // Step 5: Run simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            Log.printLine("MySimulation finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    // === Code Snippet-1 ===
    private static void createFogDevices() {
        FogDevice cloud = createAFogDevice("cloud", 44800, 40000, 100, 10000,
                0, 0.01, 16 * 103, 16 * 83.25);
        cloud.setParentId(-1);
        fogDevices.add(cloud);
        getIdByName.put(cloud.getName(), cloud.getId());

        for (int i = 0; i < numOfFogDevices; i++) {
            FogDevice device = createAFogDevice("FogDevice-" + i,
                    getValue(12000, 15000),
                    getValue(4000, 8000),
                    getValue(200, 300),
                    getValue(500, 1000),
                    1, 0.01,
                    getValue(100, 120),
                    getValue(70, 75));

            device.setParentId(cloud.getId());
            device.setUplinkLatency(10);
            fogDevices.add(device);
            getIdByName.put(device.getName(), device.getId());
        }
    }

    private static FogDevice createAFogDevice(String nodeName, long mips,
                                              int ram, long upBw, long downBw, int level,
                                              double ratePerMips, double busyPower, double idlePower) {
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));

        int hostId = FogUtils.generateEntityId();
        long storage = 1000000;
        int bw = 10000;

        PowerHost host = new PowerHost(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerOverbooking(bw),
                storage,
                peList,
                new StreamOperatorScheduler(peList),
                new FogLinearPowerModel(busyPower, idlePower)
        );

        List<Host> hostList = new ArrayList<Host>();
        hostList.add(host);

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        LinkedList<Storage> storageList = new LinkedList<Storage>();

        org.fog.entities.FogDeviceCharacteristics characteristics =
                new org.fog.entities.FogDeviceCharacteristics(
                        arch, os, vmm, host, time_zone, cost,
                        costPerMem, costPerStorage, costPerBw);

        FogDevice fogdevice = null;
        try {
            fogdevice = new FogDevice(nodeName, characteristics,
                    new AppModuleAllocationPolicy(hostList),
                    storageList, 10, upBw, downBw, 0, ratePerMips);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fogdevice.setLevel(level);
        return fogdevice;
    }

    private static int getValue(int min, int max) {
        Random rn = new Random();
        return rn.nextInt((max - min) + 1) + min;
    }
}
