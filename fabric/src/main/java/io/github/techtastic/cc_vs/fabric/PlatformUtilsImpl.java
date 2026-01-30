package io.github.techtastic.cc_vs.fabric;

import io.github.techtastic.cc_vs.fabric.config.CCVSConfig;

public class PlatformUtilsImpl {
    public static boolean canTeleport() {
        return CCVSConfig.CAN_TELEPORT.get();
    }

    public static boolean isCommandOnly() {
        return CCVSConfig.COMMAND_ONLY.get();
    }

    public static boolean exposePhysTick() {
        return CCVSConfig.EXPOSE_PHYS_TICK.get();
    }
}
