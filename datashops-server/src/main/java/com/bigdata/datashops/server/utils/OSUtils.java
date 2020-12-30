package com.bigdata.datashops.server.utils;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class OSUtils {
    private static final SystemInfo SI = new SystemInfo();

    private static HardwareAbstractionLayer hal = SI.getHardware();

    private static CentralProcessor processor = hal.getProcessor();

    public static double getLoadAvg() {
        return 0.2d;
    }

}
