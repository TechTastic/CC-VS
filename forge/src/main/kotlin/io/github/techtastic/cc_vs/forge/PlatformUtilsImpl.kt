package io.github.techtastic.cc_vs.forge

import io.github.techtastic.cc_vs.forge.config.CCVSConfig

object PlatformUtilsImpl {
    @JvmStatic
    fun canTeleport(): Boolean = CCVSConfig.CAN_TELEPORT.get()

    @JvmStatic
    fun isCommandOnly(): Boolean = CCVSConfig.COMMAND_ONLY.get()

    @JvmStatic
    fun exposePhysTick(): Boolean = CCVSConfig.EXPOSE_PHYS_TICK.get()
}