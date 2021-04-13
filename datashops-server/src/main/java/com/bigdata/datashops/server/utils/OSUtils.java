package com.bigdata.datashops.server.utils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class OSUtils {
    private static final SystemInfo SI = new SystemInfo();

    private static HardwareAbstractionLayer hal = SI.getHardware();

    private static OperatingSystem operatingSystem = SI.getOperatingSystem();

    private static CentralProcessor processor = hal.getProcessor();

    public static String getHostName() {
        return operatingSystem.getNetworkParams().getHostName();
    }

    public static long getAvailableMem() {
        return hal.getMemory().getAvailable();
    }

    public static long getTotalMem() {
        return hal.getMemory().getTotal();
    }

    public static int getPhysicalPackageCount() {
        return processor.getLogicalProcessorCount();
    }

    public static String getName() {
        return System.getProperty("os.name");
    }

    public static String getVersion() {
        return System.getProperty("os.version");
    }

    public static Map<String, Object> getCpuInfo() {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice =
                ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq =
                ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ
                                                                                               .getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL
                                                                                           .getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM
                                                                                           .getIndex()];
        long user =
                ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT
                                                                                             .getIndex()];
        long idle =
                ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        Map<String, Object> cpuInfo = Maps.newHashMap();
        cpuInfo.put("cpus", processor.getLogicalProcessorCount());
        cpuInfo.put("csys", new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        cpuInfo.put("user", new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        cpuInfo.put("iowait", new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
        cpuInfo.put("current", new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu)));
        return cpuInfo;
    }
}
